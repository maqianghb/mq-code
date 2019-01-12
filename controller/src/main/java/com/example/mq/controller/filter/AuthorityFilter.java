package com.example.mq.controller.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: 权限校验
 * @author: maqiang
 * @create: 2019/1/9
 *
 */

public class AuthorityFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(AuthorityFilter.class);

	private static List<String> SKIP_FILTER_URLS =new ArrayList<>();


	private FilterConfig filterConfig;
	private List<Filter> filters =new ArrayList<>();


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig =filterConfig;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		if (this.isNotAllowBrowser(request)) {
			LOG.warn("用户使用的浏览器版本过低，被拦截");
			return ;
		}
		String requestUri = request.getRequestURI();
		if(!CollectionUtils.isEmpty(SKIP_FILTER_URLS)){
			for(String skipUrl : SKIP_FILTER_URLS){
				if(requestUri.contains(skipUrl)){
					//跳过本次过滤器
					filterChain.doFilter(request, response);
					return;
				}
			}
		}

	}

	@Override
	public void destroy() {

	}

	private boolean isNotAllowBrowser(HttpServletRequest request) {
		String header = request.getHeader("USER-AGENT");
		return null == header ? false : 0 <= header.indexOf("MSIE") || header.contains("rv:11.0");
	}
}
