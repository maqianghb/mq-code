package com.example.mq.testcode.leedcode.filterchain;


import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/12
 *
 */
public interface Filter {

	void doFilter(MqString request, MqString response, FilterChain chain);
}
