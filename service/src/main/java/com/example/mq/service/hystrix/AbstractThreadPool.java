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
public abstract class AbstractThreadPool<T> extends HystrixCommand<T> {

	public AbstractThreadPool(String name, HystrixThreadPoolConfig config){
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(name))
				.andCommandKey(HystrixCommandKey.Factory.asKey(name))
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(name))
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
						.withQueueSizeRejectionThreshold(config.getQueueRejectSize())
				)
		);
	}
}
