package com.example.mq.testcode.filterchain;

import java.util.ArrayList;
import java.util.List;

import com.example.mq.testcode.classloader.MqString;


/**
 * @program: mq-code
 * @description: 职责链与过滤器
 * @author: maqiang
 * @create: 2019/2/12
 *
 */

public class FilterChain {

	private List<MqFilter> filters =new ArrayList<>();

	/**
	 * 过滤器链上的标识，记录过滤器的位置
	 */
	private int index =0;

	public FilterChain addFilter(MqFilter f){
		filters.add(f);
		return this;
	}

	public void doFilter(MqString request, MqString response){
		if(index ==filters.size()){
			return;
		}
		//拿到当前过滤器
		MqFilter filter =filters.get(index);
		index++;

		//传入FilterChain是为了保证在某一filter执行时，能继续调用FilterChain中的其他filter
		//所以Filter接口的实现中，需要有FilterChain的doFilter操作才能将过滤器一直执行下去
		filter.doFilter(request, response, this);
	}

}
