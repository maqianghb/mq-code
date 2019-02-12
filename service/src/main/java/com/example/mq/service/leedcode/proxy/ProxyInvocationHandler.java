package com.example.mq.service.leedcode.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @program: mq-code
 * @description: InvocationHandler的实现类，JDK动态代理
 * @author: maqiang
 * @create: 2019/2/12
 *
 */
public class ProxyInvocationHandler implements InvocationHandler {

	/**
	 * 目标类，即接口的实现类
	 */
	private Object target;

	public ProxyInvocationHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("------ before invoke " + method.getName());

		//反射执行代理方法
		Object result =method.invoke(target, args);

		System.out.println("------ end invoke " + method.getName());
		return result;
	}
}
