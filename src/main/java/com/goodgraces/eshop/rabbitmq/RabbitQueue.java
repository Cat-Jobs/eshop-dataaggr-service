package com.goodgraces.eshop.rabbitmq;

public class RabbitQueue {

	/**
	 * 数据改变
	 */
	public static final String DATA_CHANGE_QUEUE = "data-change-queue";

	/**
	 * 数据聚合
	 */
	public static final String AGGR_DATA_CHANGE_QUEUE = "aggr-data-change-queue";

	/**
	 * 刷新操作-数据改变
	 */
	public static final String REFRESH_DATA_CHANGE_QUEUE = "refresh-data-change-queue";

	/**
	 * 刷新操作-数据聚合
	 */
	public static final String REFRESH_AGGR_DATA_CHANGE_QUEUE = "refresh-aggr-data-change-queue";

	/**
	 * 高优先级-数据改变
	 */
	public static final String HIGH_PRIORITY_DATA_CHANGE_QUEUE = "high-priority-data-change-queue";

	/**
	 * 高优先级-数据聚合
	 */
	public static final String HIGH_PRIORITY_AGGR_DATA_CHANGE_QUEUE = "high-priority-aggr-data-change-queue";

}
