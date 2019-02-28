package com.example.mq.testcode.leedcode.proxy;

/**
 * @program: mq-code
 * @description: 接口类的一个实现
 * @author: maqiang
 * @create: 2019/2/12
 *
 */

public class ProxyInterfaceImpl implements ProxyInterface {

	@Override
	public String doProcess(String paramStr) {
		System.out.println("------ProxyInterfaceImpl do process.");
		//process 省略
		String result =paramStr + ", process end.";
		return result;
	}
}
