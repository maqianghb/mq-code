package com.example.mq.service.bean;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @program: mq-code
 * @description: customer查询条件
 * @author: maqiang
 * @create: 2019/2/27
 *
 */
@Data
public class CustomerQueryCondition {

	private Long customerNo;

	private String customerName;

	private Integer customerAge;

	//list操作较复杂，考虑单独过滤
	private String topTenSellers;

	private Integer minCostAmount;

	private Integer maxCostAmount;

	private Date minActiveTime;

	private Date maxActiveTime;
}
