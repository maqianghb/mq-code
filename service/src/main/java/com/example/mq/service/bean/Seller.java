package com.example.mq.service.bean;

import java.util.Date;

import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/22
 *
 */
@Data
public class Seller {



	/**
	 * 主键id
	 */
	private Long id;

	/**
	 * id
	 */
	private Long sellerNo;

	/**
	 * 姓名
	 */
	private String sellerName;

	private Integer sellerAge;

	private Integer sellerType;

	private String sellerDesc;

	/**
	 * 交易额前10的用户
	 */
	private String topTenCustomers;

	private String remark;

	private Integer deleted;

	private String createUser;

	private String updateUser;

	private Date createTime;

	private Date updateTime;

}
