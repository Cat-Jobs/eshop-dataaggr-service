package com.goodgraces.eshop.rabbitmq;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @description:数据聚合服务
 * @author:阮志龙
 * @date:2020年3月24日
 * @param:
 */
@Component
@RabbitListener(queues = RabbitQueue.AGGR_DATA_CHANGE_QUEUE)
public class AggrDataChangeQueueReceiver {

	@Autowired
	private JedisPool jedisPool;

	@RabbitHandler
	public void process(String message) {
		System.out.println("数据聚合服务接受到一个消息："+message);
		// 对这个message进行解析
		JSONObject messageJSONObject = JSONObject.parseObject(message);

		// 先获取data_type
		String dimType = messageJSONObject.getString("dim_type");
		if ("brand".equals(dimType)) {
			processBrandDimDataChange(messageJSONObject);
		} else if ("category".equals(dimType)) {
			processCategoryDimDataChange(messageJSONObject);
		} else if ("product_intro".equals(dimType)) {
			processProductIntroDimDataChange(messageJSONObject);
		} else if ("product".equals(dimType)) {
			processProductDimDataChange(messageJSONObject);
		}
	}

	private void processBrandDimDataChange(JSONObject messageJSONObject) {
		Long id = messageJSONObject.getLong("id");
		Jedis jedis = jedisPool.getResource();
		// 多此一举，看一下，查出来一个品牌数据，然后直接就原样写redis
		// 实际上是这样子的，我们这里是简化了数据结构和业务，实际上任何一个维度数据都不可能只有一个原子数据
		// 品牌数据，肯定是结构多变的，结构比较复杂，有很多不同的表，不同的原子数据
		// 实际上这里肯定是要将一个品牌对应的多个原子数据都从redis查询出来，然后聚合之后写入redis
		String dataJSON = jedis.get("brand_" + id);

		if (StringUtils.isNotEmpty(dataJSON)) {
			jedis.set("dim_brand_" + id, dataJSON);
		} else {
			jedis.del("dim_brand_" + id);
		}
	}

	private void processCategoryDimDataChange(JSONObject messageJSONObject) {
		Long id = messageJSONObject.getLong("id");

		Jedis jedis = jedisPool.getResource();

		// 多此一举，看一下，查出来一个品牌数据，然后直接就原样写redis
		// 实际上是这样子的，我们这里是简化了数据结构和业务，实际上任何一个维度数据都不可能只有一个原子数据
		// 品牌数据，肯定是结构多变的，结构比较复杂，有很多不同的表，不同的原子数据
		// 实际上这里肯定是要将一个品牌对应的多个原子数据都从redis查询出来，然后聚合之后写入redis
		String dataJSON = jedis.get("category_" + id);

		if (StringUtils.isNotEmpty(dataJSON)) {
			jedis.set("dim_category_" + id, dataJSON);
		} else {
			jedis.del("dim_category_" + id);
		}
	}

	private void processProductIntroDimDataChange(JSONObject messageJSONObject) {
		Long id = messageJSONObject.getLong("id");

		Jedis jedis = jedisPool.getResource();

		String dataJSON = jedis.get("product_intro_" + id);

		if (StringUtils.isNotEmpty(dataJSON)) {
			jedis.set("dim_product_intro_" + id, dataJSON);
		} else {
			jedis.del("dim_product_intro_" + id);
		}
	}

	private void processProductDimDataChange(JSONObject messageJSONObject) {
		Long id = messageJSONObject.getLong("id");

		Jedis jedis = jedisPool.getResource();
		
		//利用redis mget 进行批量查询
		List<String> results = jedis.mget("product_" + id,"product_property_" + id,"product_specification_" + id);
		String productDataJSON = results.get(0);

		if (StringUtils.isNotEmpty(productDataJSON)) {
			JSONObject productDataJSONObject = JSONObject.parseObject(productDataJSON);

			String productPropertyDataJSON = results.get(1);
			if (StringUtils.isNotEmpty(productPropertyDataJSON)) {
				productDataJSONObject.put("product_property_" + id, productPropertyDataJSON);
			}

			String productSpecificationDataJSON = results.get(2);
			if (StringUtils.isNotEmpty(productSpecificationDataJSON)) {
				productDataJSONObject.put("product_specification_" + id, productSpecificationDataJSON);
			}
			jedis.set("dim_product_" + id, productDataJSONObject.toJSONString());
		} else {
			jedis.del("dim_product_" + id);
		}
	}
}
