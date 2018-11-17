package com.example.mq.controller.bean;

import java.util.Date;
import java.util.Objects;

import com.example.mq.data.enums.CityEnum;
import com.example.mq.data.util.DateUtil;
import com.example.mq.data.util.NumberUtil;
import com.example.mq.service.bean.Customer;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/16
 *
 */
@Data
public class CustomerDTO {

	/**
	 * id
	 */
	private String customerId;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 城市代号
	 */
	private String city;

	/**
	 * 城市描述
	 */
	private String cityDesc;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 注册时间
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String registerTime;

	/**
	 * 消费总金额
	 * 单位：元
	 */
	private Double totalCostAmount;

	/**
	 * 记录创建时间
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String createTime;

	/**
	 * 记录更新时间
	 * 格式：yyyy-MM-dd HH:mm:ss
	 */
	private String updateTime;


	public static CustomerDTO convertToDTO(Customer customer){
		if(Objects.isNull(customer)){
			return new CustomerDTO();
		}
		CustomerDTO dto =new CustomerDTO();
		if(!Objects.isNull(customer.getCustomerId())){
			dto.setCustomerId(String.valueOf(customer.getCustomerId()));
		}
		dto.setName(customer.getName());
		dto.setCity(customer.getCity());
		CityEnum cityEnum =CityEnum.getByCode(customer.getCity());
		if(!Objects.isNull(cityEnum)){
			dto.setCityDesc(cityEnum.getName());
		}
		dto.setAddress(customer.getAddress());
		if(!Objects.isNull(customer.getRegisterTime())){
			dto.setRegisterTime(DateUtil.formatDateTime(new Date(customer.getRegisterTime())));
		}
		if(!Objects.isNull(customer.getTotalCostAmount())){
			dto.setTotalCostAmount(NumberUtil.div(customer.getTotalCostAmount(), 100.0));
		}
		if(!Objects.isNull(customer.getCreateTime())){
			dto.setCreateTime(DateUtil.formatDateTime(new Date(customer.getCreateTime())));
		}
		if(!Objects.isNull(customer.getUpdateTime())){
			dto.setUpdateTime(DateUtil.formatDateTime(new Date(customer.getUpdateTime())));
		}
		return dto;
	}

	public static Customer convertToCustomer(CustomerDTO dto){
		if(Objects.isNull(dto)){
			return new Customer();
		}
		Customer customer =new Customer();
		if(StringUtils.isNotEmpty(dto.getCustomerId())){
			customer.setCustomerId(Long.parseLong(dto.getCustomerId()));
		}
		customer.setName(dto.getName());
		customer.setCity(dto.getCity());
		customer.setAddress(dto.getAddress());
		if(StringUtils.isNotEmpty(dto.getRegisterTime())){
			customer.setRegisterTime(DateUtil.parseDateTime(dto.getRegisterTime()).getTime());
		}
		if(!Objects.isNull(dto.getTotalCostAmount())){
			customer.setTotalCostAmount(NumberUtil.intValue(NumberUtil.mul(dto.getTotalCostAmount(), 100.0)));
		}
		if(StringUtils.isNotEmpty(dto.getUpdateTime())){
			customer.setUpdateTime(DateUtil.parseDateTime(dto.getUpdateTime()).getTime());
		}
		return customer;
	}
}
