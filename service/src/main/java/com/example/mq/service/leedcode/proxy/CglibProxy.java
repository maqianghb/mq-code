package com.example.mq.service.leedcode.proxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * @program: mq-code
 * @description: Cglib动态代理
 * @author: maqiang
 * @create: 2019/2/12
 *
 */

public class CglibProxy implements MethodInterceptor {

	/**
	 * 被代理对象
	 */
	private Object target;

	public CglibProxy(Object target) {
		this.target = target;
	}

	public Object getInstance(){
		//Cglib工具类
		Enhancer enhancer =new Enhancer();
		//设置代理对象为父类
		enhancer.setSuperclass(target.getClass());
		enhancer.setCallback(this);
		//创建代理类，并返回
		return enhancer.create();
	}

	/**
	 * 复写的拦截方法
	 * @param o
	 * @param method
	 * @param objects
	 * @param methodProxy
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		System.out.println("------ before intercept " + method.getName());

		//注，并非通过反射机制执行代理方法，底层是将方法全部存入一个数组中，通过数组索引直接进行方法调用
		Object result =method.invoke(target, objects);

		System.out.println("------ end intercept " + method.getName());
		return result;
	}
}
