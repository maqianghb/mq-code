package com.example.mq.infra.customer.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.mq.infra.base.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 22:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "platform_customer")
public class CustomerDO extends BaseDO {

	private String customerNo;

	private String customerName;

	private String customerDesc;

	/**
	 * 客户类型
	 */
	private String customerType;

	private Integer customerAge;

	/**
	 * 消费top10的商家
	 */
	private String topTenSellers;

	/**
	 * 总消费金额, 单位：分
	 */
	private Integer totalCostAmount;

	/**
	 * 活动时间段
	 * mySQL中timestamp类型时间范围从1970年~2018年
	 */
	private Date minActiveTime;

	private Date maxActiveTime;

	private String extendInfo;

}

