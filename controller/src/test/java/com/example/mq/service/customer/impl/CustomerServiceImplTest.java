package com.example.mq.service.customer.impl;


import java.text.DecimalFormat;

import com.example.mq.data.util.NumberUtil;
import org.junit.Test;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/23
 *
 */
public class CustomerServiceImplTest {

	@Test
	public void queryAll() throws Exception{
		CustomerServiceImplTest test =new CustomerServiceImplTest();
		test.testParseNumber();
	}

	private void testParseNumber() throws Exception{
		String numStr ="100,928,185.37";
		double result = new DecimalFormat().parse(numStr).doubleValue();
		System.out.println("result:"+result);
	}
}