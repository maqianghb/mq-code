package com.example.mq.service.hystrix;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.base.util.CommonUtils;
import com.example.mq.base.util.SpringContextUtil;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.customer.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/15
 *
 */

public class MqThreadCommand extends AbstractThreadCommand<Map<String, Object>> {
	private final static Logger LOG = LoggerFactory.getLogger(MqThreadCommand.class);

	private CustomerService customerService;
	private Long requestId;

	public MqThreadCommand(long requestId, HystrixConfig config){
		super("myTest", config);
		this.requestId =requestId;
	}

	@Override
	protected Map<String, Object> run() throws Exception {
		if( null ==customerService){
			customerService = SpringContextUtil.getBean(CustomerService.class);
		}
		long customerNo =requestId.longValue();
		Customer customer =customerService.queryByCustomerNo(customerNo);
		Thread.sleep(190 + 20 * (CommonUtils.createRandomId(2)%3-1));
		Map<String, Object> result =new HashMap<>();
		result.put("customerNo", customer.getCustomerNo());
		result.put("customerName", customer.getCustomerName());
		return result;
	}

	@Override
	protected Map<String, Object> getFallback() {
		//降级，超时或熔断后执行降级操作
		LOG.error("MqThreadCommand fallback, requestId:{}", requestId);
		Map<String, Object> defaultMap =new HashMap<>();
		return defaultMap;
	}
}
