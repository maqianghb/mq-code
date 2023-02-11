package com.example.mq.data.loopthread;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/6/25
 *
 */

public class AsyncLoopRunnable implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(AsyncLoopRunnable.class);

	private static AtomicBoolean shutdown =new AtomicBoolean(false);
	private static AtomicBoolean shutdowned =new AtomicBoolean(false);

	public static AtomicBoolean getShutdown() {
		return shutdown;
	}

	private RunnableCallback function;
	private RunnableCallback killFunction;

	public AsyncLoopRunnable(RunnableCallback function, RunnableCallback killFunction) {
		this.function = function;
		this.killFunction = killFunction;
	}

	@Override
	public void run() {
		if(null ==function){
			LOG.error(" function is null.");
			throw new RuntimeException("function of asyncLoopRunnable is null.");
		}

		function.preRun();

		try {
			while (!shutdown.get()){
				function.run();
				if(shutdown.get()){
					this.shutdown();
					return;
				}
			}
		} catch (Exception e) {
			if (shutdown.get()) {
				shutdown();
			} else {
				LOG.error("Async loop died!!!" + e.getMessage(), e);
				killFunction.execute(e);
			}
		}
	}

	private void shutdown(){

	}

}
