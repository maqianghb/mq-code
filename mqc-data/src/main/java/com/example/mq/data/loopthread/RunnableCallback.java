package com.example.mq.data.loopthread;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/6/25
 *
 */

public class RunnableCallback {

	public <T> Object execute(T... args) {
		return null;
	}

	/**
	 * 预执行
	 */
	public void preRun() {
	}

	public void run() {
	}

	/**
	 * 后执行
	 */
	public void afterRun() {
	}

	public Object getResult() {
		return null;
	}

	public String getThreadName() {
		return null;
	}

}
