package com.example.mq.data.loopthread;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/7/3
 *
 */

public class AsyncLoopThread {
	private static final Logger LOG = LoggerFactory.getLogger(AsyncLoopThread.class);

	private Thread thread;
	private RunnableCallback afterRunCallBack;

	public AsyncLoopThread(RunnableCallback afn, boolean daemon, RunnableCallback kill_fn, int priority, boolean start) {
		this.init(afn, daemon, kill_fn, priority, start);
	}


	private void init(RunnableCallback afn, boolean daemon, RunnableCallback kill_fn, int priority, boolean start) {
		if (kill_fn == null) {
			throw new IllegalArgumentException("kill_fn is null.");
		}

		Runnable runnable = new AsyncLoopRunnable(afn, kill_fn);
		thread = new Thread(runnable);
		String threadName = StringUtils.isEmpty(afn.getThreadName()) ? afn.getClass().getSimpleName() : afn.getThreadName();
		thread.setName(threadName);
		thread.setDaemon(daemon);
		thread.setPriority(priority);
		thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				LOG.error("UncaughtException", e);
			}
		});

		this.afterRunCallBack = afn;

		if (start) {
			thread.start();
		}
	}


}
