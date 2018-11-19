package com.example.mq.service.bean;


import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:15
 */
@Data
public class Customer {

	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * id
	 */
	private Long customerId;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 城市
	 */
	private String city;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 注册时间
	 */
	private Long registerTime;

	/**
	 * 消费总金额
	 * 单位：分
	 */
	private Integer totalCostAmount;

	/**
	 * 记录创建时间
	 */
	private Long createTime;

	/**
	 * 记录更新时间
	 */
	private Long updateTime;

}

