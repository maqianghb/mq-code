package com.example.mq.service.bean;


import java.util.Date;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:15
 */
@Data
public class Customer {

	private Long id;

	private Long customerNo;

	private String customerName;

	private Integer customerAge;

	private Integer customerType;

	private String customerDesc;

	/**
	 * 消费top10的商家
	 */
	private String topTenSellers;

	/**
	 * 总消费金额
	 * 单位：分
	 */
	private Integer totalCostAmount;

	/**
	 * 活动时间段
	 */
	private Date minActiveTime;

	private Date maxActiveTime;

	private String remark;

	private Integer deleted;

	private String md5;


	private String createUser;

	private String updateUser;

	private Date createTime;

	private Date updateTime;

}

