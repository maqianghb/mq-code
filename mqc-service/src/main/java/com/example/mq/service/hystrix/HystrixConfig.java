package com.example.mq.service.hystrix;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/8
 *
 */
@Data
public class HystrixConfig {

	/**
	 * 执行超时时间
	 */
	private Integer executeTimeOutInMillis;

	/**
	 * 核心线程池
	 */
	private Integer corePoolSize;

	/**
	 * 线程池最大等待队列
	 */
	private Integer maxQueueSize;

	/**
	 * 最大线程池
	 */
	private Integer maxPoolSize;

	/**
	 * 空闲线程存活时间
	 */
	private Integer keepAliveTime;

	/**
	 * 队列拒绝任务数大小
	 */
	private Integer queueRejectSize;

	/**
	 * 是否开启熔断
	 */
	private Boolean circuitBreakerEnabled;

	/**
	 * 熔断错误百分比
	 */
	private Integer cbErrorThresholdPercent;

	/**
	 * 开始熔断请求最小阈值
	 */
	private Integer cbRequestVolumeThreshold;

	/**
	 * 熔断后半开试探休眠时间
	 */
	private Integer cbSleepWindowInMillis;
}
