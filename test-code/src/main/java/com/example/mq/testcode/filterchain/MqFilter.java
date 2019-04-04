package com.example.mq.testcode.filterchain;


import com.example.mq.testcode.classloader.MqString;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/12
 *
 */
public interface MqFilter {

	void doFilter(MqString request, MqString response, FilterChain chain);
}
