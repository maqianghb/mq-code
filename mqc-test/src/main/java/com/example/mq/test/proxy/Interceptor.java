package com.example.mq.testcode.proxy;

import java.lang.reflect.Method;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/11/11
 *
 */
public interface Interceptor {

	public void intercept(Method method, Object[] args);
}
