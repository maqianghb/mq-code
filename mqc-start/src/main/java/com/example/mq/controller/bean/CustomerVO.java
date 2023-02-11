package com.example.mq.controller.bean;

import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.DateUtil;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.core.domain.customer.model.Customer;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/16
 *
 */
@Data
public class CustomerVO {

	private String id;

	private String customerNo;

	private String customerName;

	private Integer customerAge;

	private Integer customerType;

	private String customerDesc;

	/**
	 * 消费top10的商家
	 */
	private List<String> topTenSellers;

	/**
	 * 总消费金额
	 * 单位：分
	 */
	private Double totalCostAmount;

	/**
	 * 活动时间段
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String minActiveTime;

	private String maxActiveTime;

	private String remark;

	private Integer deleted;

	private String createUser;

	private String updateUser;

	/**
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String createTime;

	/**
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String updateTime;


	public static CustomerVO convertToVO(Customer customer){
		if(Objects.isNull(customer)){
			return new CustomerVO();
		}
		CustomerVO vo =new CustomerVO();
		if(null !=customer.getId()){
			vo.setId(String.valueOf(customer.getId()));
		}
		if(null !=customer.getCustomerNo()){
			vo.setCustomerNo(String.valueOf(customer.getCustomerNo()));
		}
		vo.setCustomerName(customer.getCustomerName());
		vo.setCustomerAge(customer.getCustomerAge());
		vo.setCustomerType(customer.getCustomerType());
		vo.setCustomerDesc(customer.getCustomerDesc());
		if(!StringUtils.isEmpty(customer.getTopTenSellers())){
			vo.setTopTenSellers(JSONObject.parseArray(customer.getTopTenSellers(), String.class));
		}
		if(null !=customer.getTotalCostAmount()){
			vo.setTotalCostAmount(NumberUtil.div(customer.getTotalCostAmount(), 100.0));
		}
		if(null != customer.getMinActiveTime()){
			vo.setMinActiveTime(DateUtil.formatDateTime(customer.getMinActiveTime()));
		}
		if(null != customer.getMaxActiveTime()){
			vo.setMaxActiveTime(DateUtil.formatDateTime(customer.getMaxActiveTime()));
		}
		vo.setRemark(customer.getRemark());
		vo.setDeleted(customer.getDeleted());

		vo.setCreateUser(customer.getCreateUser());
		vo.setUpdateUser(customer.getUpdateUser());
		if(null != customer.getCreateTime()){
			vo.setCreateTime(DateUtil.formatDateTime(customer.getCreateTime()));
		}
		if(null != customer.getUpdateTime()){
			vo.setUpdateTime(DateUtil.formatDateTime(customer.getUpdateTime()));
		}
		return vo;
	}

	public static Customer convertToCustomer(CustomerVO vo){
		if(Objects.isNull(vo)){
			return new Customer();
		}
		Customer customer =new Customer();
		if(!StringUtils.isEmpty(vo.getId())){
			customer.setId(Long.parseLong(vo.getId()));
		}
		if(!StringUtils.isEmpty(vo.getCustomerNo())){
			customer.setCustomerNo(Long.parseLong(vo.getCustomerNo()));
		}
		customer.setCustomerName(vo.getCustomerName());
		customer.setCustomerAge(vo.getCustomerAge());
		customer.setCustomerType(vo.getCustomerType());
		customer.setCustomerDesc(vo.getCustomerDesc());
		if(!CollectionUtils.isEmpty(vo.getTopTenSellers())){
			customer.setTopTenSellers(JSONObject.toJSONString(vo.getTopTenSellers()));
		}
		if(null !=vo.getTotalCostAmount()){
			customer.setTotalCostAmount(NumberUtil.intValue(NumberUtil.mul(vo.getTotalCostAmount(), 100.0)));
		}
		if(null != vo.getMinActiveTime()){
			customer.setMinActiveTime(DateUtil.parseDateTime(vo.getMinActiveTime()));
		}
		if(null != vo.getMaxActiveTime()){
			customer.setMaxActiveTime(DateUtil.parseDateTime(vo.getMaxActiveTime()));
		}
		customer.setRemark(vo.getRemark());
		customer.setDeleted(vo.getDeleted());

		customer.setCreateUser(vo.getCreateUser());
		customer.setUpdateUser(vo.getUpdateUser());
		if(null != vo.getCreateTime()){
			customer.setCreateTime(DateUtil.parseDateTime(vo.getCreateTime()));
		}
		if(null != vo.getUpdateTime()){
			customer.setUpdateTime(DateUtil.parseDateTime(vo.getUpdateTime()));
		}
		return customer;
	}
}
