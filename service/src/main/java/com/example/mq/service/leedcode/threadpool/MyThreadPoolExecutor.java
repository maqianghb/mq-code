package com.example.mq.service.leedcode.threadpool;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: 自定义threadpool
 * @author: maqiang
 * @create: 2018/11/16
 *
 */

public class MyThreadPoolExecutor extends ThreadPoolExecutor {
	private static final Logger LOG = LoggerFactory.getLogger("MyThreadPoolExecutor");

	private String threadPoolName;
	private final ThreadLocal<Long> startTime =new ThreadLocal<>();

	public MyThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.threadPoolName = name;
	}

	public MyThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		this.threadPoolName = name;
	}

	public MyThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		this.threadPoolName = name;
	}

	public MyThreadPoolExecutor(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		this.threadPoolName = name;
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		startTime.set(System.currentTimeMillis());
		super.beforeExecute(t, r);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		if(!Objects.isNull(t)){
			LOG.error("MyThreadPoolExecutor, threadPoolName:{}, error:{}", threadPoolName, t.getLocalizedMessage());
		}
		super.afterExecute(r, t);
		if(!Objects.isNull(startTime) && startTime.get() >0){
			LOG.info("name:{}, cost:{}", threadPoolName, System.currentTimeMillis()-startTime.get());
		}
	}
}
