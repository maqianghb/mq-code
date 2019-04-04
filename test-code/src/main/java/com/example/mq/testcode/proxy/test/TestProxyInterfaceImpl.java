package com.example.mq.testcode.proxy.test;

/**
 * @program: mq-code
 * @description: 接口类的一个实现
 * @author: maqiang
 * @create: 2019/2/12
 *
 */

public class TestProxyInterfaceImpl implements TestProxyInterface {

	@Override
	public String doProcess(String paramStr) {
		System.out.println("------TestProxyInterfaceImpl do process.");
		//process 省略
		String result =paramStr + ", process end.";
		return result;
	}
}
