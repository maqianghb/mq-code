package com.example.mq.testcode.zk;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/5
 *
 */

public class SharedReentrantLockDemo {
	private static final Logger LOG = LoggerFactory.getLogger(SharedReentrantLockDemo.class);

	private static final String CONN_ADDS ="localhost:2181,localhost:2182,localhost:2183";
	private static final Integer SESSION_TIMEOUT = 20*1000;
	private static final Integer CONN_TIMEOUT = 5*1000;
	private static final Integer SLEEP_TIME = 3*1000;
	private static final Integer MAX_RETRIES = 5;

	private CuratorFramework zkClient;

	private void init(){
		zkClient = CuratorFrameworkFactory.builder()
				.connectString(CONN_ADDS)
				.sessionTimeoutMs(SESSION_TIMEOUT)
				.connectionTimeoutMs(CONN_TIMEOUT)
				.retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME, MAX_RETRIES))
				.build();
		zkClient.start();
	}

	private void testSharedLock(){
		String lockPath ="/testSharedLockPath";
		InterProcessMutex sharedLock =new InterProcessMutex(zkClient, lockPath);
		try {
			if(sharedLock.acquire(100, TimeUnit.MILLISECONDS)){
				System.out.println(Thread.currentThread().getName()+ " get sharedLock.");
				Thread.sleep(10 *1000L);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(sharedLock.isAcquiredInThisProcess()){
					System.out.println(Thread.currentThread().getName() + " release sharedLock. ");
					sharedLock.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		SharedReentrantLockDemo lockDemo = new SharedReentrantLockDemo();
		lockDemo.init();
		Executor executor = new ThreadPoolExecutor(3, 5, 60, TimeUnit.MINUTES,
				new LinkedBlockingQueue<>(100));
		for (int i = 0; i < 100; i++) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					lockDemo.testSharedLock();
				}
			});
			lockDemo.testSharedLock();
		}
	}



}
