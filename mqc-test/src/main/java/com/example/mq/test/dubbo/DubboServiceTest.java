package com.example.mq.test.dubbo;


import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.fastjson.JSONObject;

import com.example.mq.app.customer.CustomerService;
import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.client.customer.request.CustomerRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/8
 *
 */
@Slf4j
public class DubboServiceTest {
	private static final String APPLICATION_NAME ="test_application";
	private static final String DEV_ZK_URL = "127.0.0.1:12345";

	public static void main(String[] args){
		DubboServiceTest dubboServiceTest = new DubboServiceTest();

		dubboServiceTest.testQueryCustomer();
		System.out.println("------test end.");
	}

	private void testQueryCustomer(){
		CustomerRequest request =new CustomerRequest();
		request.setCustomerNo("123456");

		try {
			CustomerService customerService = DubboServiceTest.getDubboService(CustomerService.class, null);
			CustomerDTO customerDTO = customerService.queryByCustomerNo(request);
		} catch (Exception e) {
			log.error("客户信息查询失败，request:{}", JSONObject.toJSONString(request), e);
		}
	}

	private static <T> T getDubboService(Class clazz, String version) throws ClassNotFoundException {
		ApplicationConfig application = new ApplicationConfig();
		application.setName(APPLICATION_NAME);
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress(DEV_ZK_URL);
		ReferenceConfig<T> rc = new ReferenceConfig<T>();
		rc.setApplication(application);
		rc.setRegistry(registry);
		rc.setInterface(clazz.getName());
		rc.setUrl("dubbo://127.0.0.1:12345/com.example.mq.web.CustomerServiceApi");
		if (StringUtils.isNotBlank(version)) {
			rc.setVersion(version);
		}
		rc.setProtocol("dubbo");
		rc.setTimeout(3000);
		return rc.get();
	}
}
