package com.example.mq.core.manager.customer.model;


import com.example.mq.core.manager.BaseDO;
import lombok.Data;

import java.util.Date;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:15
 */
@Data
public class CustomerDO extends BaseDO {

	private Long id;

	private Long customerNo;

	private String customerName;

	private Integer customerAge;

	/**
	 * 设置默认值，即类型默认为1
	 */
	private Integer customerType =1;

	private String customerDesc;

	/**
	 * 消费top10的商家
	 */
	private String topTenSellers;

	/**
	 * 总消费金额
	 * 单位：分
	 */
	private Integer totalCostAmount =0;

	/**
	 * 活动时间段
	 * mySQL中timestamp类型时间范围从1970年~2018年
	 */
	private Date minActiveTime;

	private Date maxActiveTime;

	private String remark;

	private Integer deleted =0;

	private String md5;

	private String createUser;

	private String updateUser;

	private Date createTime;

	private Date updateTime;

}

