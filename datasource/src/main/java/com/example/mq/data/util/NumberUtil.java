package com.example.mq.data.util;

import java.math.BigDecimal;

/**
 * @program: mq-code
 * @description: 金额操作类
 * @author: maqiang
 * @create: 2018/11/14
 *
 */

public class NumberUtil {

	/**
	 * 默认精度
	 */
	private static final int DEFAULT_SCALE=2;

	/**
	 * 加
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double add(double v1 ,double v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.add(b2).doubleValue();
	}

	/**
	 * 减
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double sub(double v1 ,double v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 乘
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double mul(double v1 ,double v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 除
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double div(double v1 ,double v2){
		BigDecimal b1=new BigDecimal(v1);
		BigDecimal b2=new BigDecimal(v2);
		return b1.divide(b2, DEFAULT_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * format:2位小数，四舍五入
	 * @param v1
	 * @return
	 */
	public static double format(double v1){
		return div(v1, 1.0);
	}

	/**
	 * double转int，四舍五入
	 * @param v
	 * @return
	 */
	public static int intValue(double v){
		return Long.valueOf(Math.round(v)).intValue();
	}

	public static void main(String[] args) throws Exception{
		double num1 =1.00001;
		double num2 = 0.50001;
		System.out.println("------ add result:"+ NumberUtil.add(num1, num2));
		System.out.println("------ sub result:"+ NumberUtil.sub(num1, num2));
		System.out.println("------ mul result:"+ NumberUtil.mul(num1, num2));
		System.out.println("------ div result:"+ NumberUtil.div(num1, num2));
		System.out.println("------ add result parse int:"+ NumberUtil.intValue(NumberUtil.add(num1, num2)));
		System.out.println("------ format result:"+ NumberUtil.format(num1));
		System.out.println("------ format result:"+ NumberUtil.format(num2));

	}
}

