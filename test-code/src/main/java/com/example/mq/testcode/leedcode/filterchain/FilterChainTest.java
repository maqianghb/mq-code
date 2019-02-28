package com.example.mq.testcode.leedcode.filterchain;

import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/12
 *
 */

public class FilterChainTest {

	public static void main(String[] args){
		FilterChainTest filterChainTest =new FilterChainTest();
		filterChainTest.doTest();
	}

	private void doTest(){
		MqString request =new MqString("---12=++3--==++4=5-++-67+==8--9+=++=");
		MqString response =new MqString("---12=++3--==++4=5-++-67+==8--9+=++=");

		FilterChain chain =new FilterChain();
		chain.addFilter(new MinusFilter())
				.addFilter(new PlusFilter())
				.addFilter(new EqualsFilter());

		chain.doFilter(request, response);
		System.out.println("filterResult: request:" + request.getValue());
		System.out.println("filterResult: response:" + response.getValue());
	}

	class MinusFilter implements Filter{

		@Override
		public void doFilter(MqString request, MqString response, FilterChain chain) {
			System.out.println("minusFilter start.");
			//请求过滤减号
			request.setValue(request.getValue().replace("-", ""));
			System.out.println("request:"+request.getValue());

			//filterChain
			chain.doFilter(request, response);

			//结果过滤减号
			response.setValue(response.getValue().replace("-", ""));
			System.out.println("response:"+response.getValue());

			System.out.println("minusFilter end.");
		}
	}

	class PlusFilter implements Filter{

		@Override
		public void doFilter(MqString request, MqString response, FilterChain chain) {
			System.out.println("plusFilter start.");
			//请求过滤加号
			request.setValue(request.getValue().replace("+", ""));
			System.out.println("request:"+request.getValue());

			//filterChain
			chain.doFilter(request, response);

			//结果过滤加号
			response.setValue(response.getValue().replace("+", ""));
			System.out.println("response:"+response.getValue());

			System.out.println("plusFilter end.");
		}
	}

	class EqualsFilter implements Filter{

		@Override
		public void doFilter(MqString request, MqString response, FilterChain chain) {
			System.out.println("equalsFilter start.");
			//请求过滤等号
			request.setValue(request.getValue().replace("=", ""));
			System.out.println("request:"+request.getValue());

			//filterChain
			chain.doFilter(request, response);

			//结果过滤等号
			response.setValue(response.getValue().replace("=", ""));
			System.out.println("response:"+response.getValue());

			System.out.println("equalsFilter end.");
		}
	}
}
