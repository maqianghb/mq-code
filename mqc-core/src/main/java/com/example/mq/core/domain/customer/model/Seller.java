package com.example.mq.core.domain.customer.model;

import java.util.Date;

import com.example.mq.core.domain.BaseDomain;
import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/22
 *
 */
@Data
public class Seller extends BaseDomain {



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

	private Integer deleted =0;

	private String createUser;

	private String updateUser;

	private Date createTime;

	private Date updateTime;

}
