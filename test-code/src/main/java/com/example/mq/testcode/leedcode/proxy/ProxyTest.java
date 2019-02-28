package com.example.mq.testcode.leedcode.proxy;

import java.lang.reflect.Proxy;

/**
 * @program: mq-code
 * @description: 动态代理测试
 * @author: maqiang
 * @create: 2019/2/12
 *
 */

public class ProxyTest {

	public static void main(String[] args){
		ProxyTest proxyTest =new ProxyTest();
		proxyTest.testJDKProxy();
		proxyTest.testCglibProxy();
	}

	private void testJDKProxy(){
		ProxyInvocationHandler invocationHandler =new ProxyInvocationHandler(new ProxyInterfaceImpl());
		//绑定接口的实现类到代理对象
		ProxyInterface proxy =(ProxyInterface) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[]{ProxyInterface.class}, invocationHandler);
		String result =proxy.doProcess("test jdk proxy");
		System.out.println("------result:" + result);
	}

	private void testCglibProxy(){
		CglibProxy proxy =new CglibProxy(new ProxyInterfaceImpl());
		ProxyInterfaceImpl impl =(ProxyInterfaceImpl) proxy.getInstance();
		String result =impl.doProcess("test cglib proxy");
		System.out.println("------result:" + result);
	}
}
