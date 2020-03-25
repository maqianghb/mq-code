package com.example.mq.testcode.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/11/11
 *
 */

public class TargetProxy implements InvocationHandler {

	private Object target;

	private Interceptor interceptor;

	public TargetProxy(Object target, Interceptor interceptor) {
		this.target = target;
		this.interceptor = interceptor;
	}

	// 将拦截逻辑封装到拦截器中，有客户端生成目标类的代理类的时候一起传入，这样客户端就可以设置不同的拦截逻辑。
	public static Object bind(Object target, Interceptor interceptor){
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
				new TargetProxy(target, interceptor));
	}

	@Override
	// 在执行目标对象方法前加上自己的拦截逻辑
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 执行客户端定义的拦截逻辑
		interceptor.intercept(method, args);
		return method.invoke(target, args);
	}
}
