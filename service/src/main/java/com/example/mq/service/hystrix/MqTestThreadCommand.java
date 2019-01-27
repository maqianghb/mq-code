package com.example.mq.service.hystrix;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.data.util.SpringContextUtil;
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

public class MqTestThreadCommand extends AbstractThreadCommand<Map<String, Object>> {
	private final static Logger LOG = LoggerFactory.getLogger(MqTestThreadCommand.class);

	private CustomerService customerService;
	private Long requestId;

	public MqTestThreadCommand(Long requestId, HystrixConfig config){
		super("myTest", config);
		this.requestId =requestId;
		customerService = SpringContextUtil.getBean("customerService", CustomerService.class);
	}

	@Override
	protected Map<String, Object> run() throws Exception {
		Customer customer =customerService.queryByCustomerId(requestId);
		LOG.info("before thread sleep, requestId:{}|customer:{}", requestId, JSONObject.toJSONString(customer));
		Thread.sleep(2 * 1000);
		LOG.info("after thread sleep, requestId:{}|customer:{}", requestId, JSONObject.toJSONString(customer));
		Map<String, Object> result =new HashMap<>();
		result.put("customerId", customer.getCustomerId());
		result.put("name", customer.getName());
		return result;
	}

	@Override
	protected Map<String, Object> getFallback() {
		LOG.error("MqTestThreadCommand fall back, requestId:{}", requestId);
		Map<String, Object> defaultMap =new HashMap<>();
		return defaultMap;
	}
}