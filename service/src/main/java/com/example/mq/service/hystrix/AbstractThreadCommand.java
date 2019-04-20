package com.example.mq.service.hystrix;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * @program: mq-code
 * @description: 线程池隔离，限流，熔断
 * @author: maqiang
 * @create: 2019/1/8
 *
 */
public abstract class AbstractThreadCommand<T> extends HystrixCommand<T> {

	private final static String  SUFFIX_GROUP_KEY= "_group";
	private final static String SUFFIX_COMMAND_KEY = "_commandKey";
	private final static String SUFFIX_THREAD_POOL_KEY = "_threadPool";

	public AbstractThreadCommand(String name, HystrixConfig config){
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(name + SUFFIX_GROUP_KEY))
				.andCommandKey(HystrixCommandKey.Factory.asKey(name + SUFFIX_COMMAND_KEY))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(name + SUFFIX_THREAD_POOL_KEY))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(config.getExecuteTimeOutInMillis())
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
						//即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝，默认值5
						.withQueueSizeRejectionThreshold(config.getQueueRejectSize())
				)
		);
	}
}
