package com.example.mq.controller.bean;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.common.utils.NumberUtil;
import com.example.mq.service.bean.CustomerQueryCondition;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/28
 *
 */
@Data
public class CustomerQueryConditionVO {

	private Long customerNo;

	private String customerName;

	private Integer customerAge;

	private List<String> topTenSellers;

	private Double minCostAmount;

	private Double maxCostAmount;

	private String minActiveTime;

	private String maxActiveTime;

	public static CustomerQueryCondition convertToCondition(CustomerQueryConditionVO vo){
		if(null ==vo){
			return new CustomerQueryCondition();
		}
		CustomerQueryCondition condition =new CustomerQueryCondition();
		condition.setCustomerNo(vo.getCustomerNo());
		condition.setCustomerName(vo.getCustomerName());
		condition.setCustomerAge(vo.getCustomerAge());
		if(!CollectionUtils.isEmpty(vo.getTopTenSellers())){
			condition.setTopTenSellers(JSONObject.toJSONString(vo.getTopTenSellers()));
		}
		if(null !=vo.getMinCostAmount()){
			condition.setMinCostAmount(NumberUtil.intValue(NumberUtil.mul(vo.getMinCostAmount(), 100.0)));
		}
		if(null !=vo.getMaxCostAmount()){
			condition.setMaxCostAmount(NumberUtil.intValue(NumberUtil.mul(vo.getMaxCostAmount(), 100.0)));
		}
		if(!StringUtils.isEmpty(vo.getMinActiveTime())){
			condition.setMinActiveTime(DateUtil.parseDateTime(vo.getMinActiveTime()));
		}
		if(!StringUtils.isEmpty(vo.getMaxActiveTime())){
			condition.setMaxActiveTime(DateUtil.parseDateTime(vo.getMaxActiveTime()));
		}
		return condition;
	}

}
