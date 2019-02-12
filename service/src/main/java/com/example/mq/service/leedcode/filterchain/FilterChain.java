package com.example.mq.service.leedcode.filterchain;

import java.util.ArrayList;
import java.util.List;


/**
 * @program: mq-code
 * @description: 职责链与过滤器
 * @author: maqiang
 * @create: 2019/2/12
 *
 */

public class FilterChain {

	private List<Filter> filters =new ArrayList<>();

	private int index =0;

	public FilterChain addFilter(Filter f){
		filters.add(f);
		return this;
	}

	public void doFilter(MqString request, MqString response){
		if(index ==filters.size()){
			return;
		}
		Filter filter =filters.get(index);
		index++;

		//传入FilterChain是为了保证在某一filter执行时，能继续调用FilterChain中的其他filter
		//所以Filter接口的实现中，需要有FilterChain的doFilter操作才能将过滤器一直执行下去
		filter.doFilter(request, response, this);
	}

}
