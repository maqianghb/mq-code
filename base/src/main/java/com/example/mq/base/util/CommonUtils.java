package com.example.mq.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/19
 *
 */

public class CommonUtils {
	private static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

	public static long createRandomId(int length){
		if(length <=0){
			return 0;
		}
		//1.000001~9.999999
		double d1 =Math.random() *9 +1;
		for(int i=0; i<length; i++){
			d1 =d1 *10;
		}
		return (long) d1;
	}
}
