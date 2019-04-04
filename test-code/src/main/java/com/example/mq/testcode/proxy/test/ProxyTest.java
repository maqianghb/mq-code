package com.example.mq.testcode.proxy.test;

import java.lang.reflect.Proxy;

import com.example.mq.testcode.proxy.MqCglibProxy;
import com.example.mq.testcode.proxy.MqProxyInvocationHandler;

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
		MqProxyInvocationHandler invocationHandler =new MqProxyInvocationHandler(new TestProxyInterfaceImpl());
		//绑定接口的实现类到代理对象
		TestProxyInterface proxy =(TestProxyInterface) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[]{TestProxyInterface.class}, invocationHandler);
		String result =proxy.doProcess("test jdk proxy");
		System.out.println("------result:" + result);
	}

	private void testCglibProxy(){
		MqCglibProxy proxy =new MqCglibProxy(new TestProxyInterfaceImpl());
		TestProxyInterfaceImpl impl =(TestProxyInterfaceImpl) proxy.getInstance();
		String result =impl.doProcess("test cglib proxy");
		System.out.println("------result:" + result);
	}
}
