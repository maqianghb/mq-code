package com.example.mq.service.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * @program: mq-code
 * @description: 信号量隔离，限流，熔断
 * @author: maqiang
 * @create: 2019/1/8
 *
 */

public abstract class AbstractSemaphoreCommand<T> extends HystrixCommand<T> {

	private final static String  SUFFIX_GROUP_KEY= "_group";
	private final static String SUFFIX_COMMAND_KEY = "_commandKey";
	private final static String SUFFIX_SEMAPHORE_KEY = "_threadPool";

	public AbstractSemaphoreCommand(String name, HystrixConfig config){
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(name + SUFFIX_GROUP_KEY))
				.andCommandKey(HystrixCommandKey.Factory.asKey(name + SUFFIX_COMMAND_KEY))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(name + SUFFIX_SEMAPHORE_KEY))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						//请求超时时间设置
						.withExecutionTimeoutInMilliseconds(config.getCbSleepWindowInMillis())
						//熔断配置
						.withCircuitBreakerEnabled(config.getCircuitBreakerEnabled())
						.withCircuitBreakerErrorThresholdPercentage(config.getCbErrorThresholdPercent())
						.withCircuitBreakerRequestVolumeThreshold(config.getCbRequestVolumeThreshold())
						.withCircuitBreakerSleepWindowInMilliseconds(config.getCbSleepWindowInMillis())
				)
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						//线程池配置
						.withCoreSize(config.getCorePoolSize())
						.withMaxQueueSize(config.getMaxQueueSize())
						.withMaximumSize(config.getMaxQueueSize())
						.withKeepAliveTimeMinutes(config.getKeepAliveTime())
						.withQueueSizeRejectionThreshold(config.getQueueRejectSize())
				)
		);
	}
}
