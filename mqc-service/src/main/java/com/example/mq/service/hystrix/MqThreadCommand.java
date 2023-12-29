package com.example.mq.service.hystrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.mq.core.domain.customer.model.Customer;
import com.example.mq.data.dubbo.TraceContextUtils;
import com.example.mq.common.utils.CommonUtils;
import com.example.mq.common.utils.SpringContextUtil;
import com.example.mq.service.customer.CustomerDomainService;
import com.google.common.collect.Lists;
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

	private CustomerDomainService customerDomainService;
	private Long requestId;

	public MqThreadCommand(long requestId, HystrixConfig config){
		super("myTest", config);
		this.requestId =requestId;
	}

	@Override
	protected Map<String, Object> run() throws Exception {
		TraceContextUtils.setLocalTraceContext(traceContext);

		if( null == customerDomainService){
			customerDomainService = SpringContextUtil.getBean(CustomerDomainService.class);
		}
		long customerNo =requestId.longValue();
		List<Customer> customerList = customerDomainService.queryCustomerList(new Customer());
		Customer customer = Optional.ofNullable(customerList).orElse(Lists.newArrayList()).stream()
				.findFirst()
				.orElse(null);

		// thread sleep
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
