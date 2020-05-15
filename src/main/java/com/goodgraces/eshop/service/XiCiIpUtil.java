package com.goodgraces.eshop.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.stereotype.Component;

/**
 * 西刺代理 一次只能取到 20页 *10 2000条数据 https://www.xicidaili.com/nn/1
 */
@Component
public class XiCiIpUtil {
//	private String startUrl = "https://www.xicidaili.com/nn";// .com/nn/1....n
	private String startUrl="https://www.xicidaili.com/nn";
	@Test
	public void doXiCiIpUtil() {
		try {
			parseHtml(startUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析html页面
	 * 
	 * @param html
	 */
	private void parseHtml(String html) throws Exception {
		for (int i = 1; i <= 20; i++) {// 取20页数据
			// 解析页面的数据
			Document document = Jsoup.connect(html).timeout(1000*60).proxy("118.24.155.27", 8118).get();
			System.out.println(document.baseUri());
			Elements items = document.select("#ip_list").get(0).select("tr");
			forEachData(items);
			Thread.sleep(2000);// 休眠2秒
		}

	}

	// 5)把抓取到的ip 存到数据库里
	public int forEachData(Elements items) {
		// delete();//先删除表内数据
		int count = 0;
		try {

			for (int i = 1; i < items.size(); i++) {// 每页显示101条数据 从第一条开始
				Elements elements = items.get(i).select("td");
				String ip = elements.get(1).text();// IP
				String port = elements.get(2).text();// 端口
				String server = elements.get(3).text();// 服务器类型
				String type = elements.get(5).text();// 类型
				String fast = elements.get(6).select("div").get(0).attributes().get("title");// 速度
				count++;
				System.out.println(count + "--"+fast+ "抓取成功");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
}
