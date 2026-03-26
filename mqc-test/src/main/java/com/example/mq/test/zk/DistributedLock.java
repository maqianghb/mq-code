package com.example.mq.testcode.zk;

import java.util.concurrent.TimeUnit;

/**
 * @program: mq-code
 * @description: 分布式锁接口
 * @author: maqiang
 * @create: 2018/10/29
 *
 */
public interface DistributedLock {

	/**
	 * 	获取锁直至成功
	 * @throws Exception
	 */
	void acquire() throws Exception;

	/**
	 * 获取锁，直至超时
	 * @param time
	 * @param unit
	 * @return
	 * @throws Exception
	 */
	Boolean acquire(long time, TimeUnit unit) throws Exception;

	/**
	 * 释放锁
	 * @throws Exception
	 */
	void release() throws Exception;
}
