package com.example.mq.testcode.dubbo;


import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.client.common.Result;
import com.example.mq.client.service.customer.CustomerService;
import com.example.mq.client.service.customer.request.CustomerRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: CustomerApiService接口测试类
 * @author: maqiang
 * @create: 2019/4/8
 *
 */

public class CustomerApiServiceTest {
	private static Logger LOG = LoggerFactory.getLogger(CustomerApiServiceTest.class);

	private static final String APPLICATION_NAME ="test_application";
	private static final String DEV_ZK_URL = "127.0.0.1:12345";
	private static final String TEST_ZK_URL ="127.0.0.1:12345";

	public static void main(String[] args){
		CustomerApiServiceTest customerApiServiceTest = new CustomerApiServiceTest();
		customerApiServiceTest.testQueryCustomer();

		System.out.println("------test end.");
	}

	private void testQueryCustomer(){
		CustomerRequest request =new CustomerRequest();
		request.setCode("123456789");

		Result result =null;
		try {
			CustomerService customerService = CustomerApiServiceTest.getDubboService(CustomerService.class, null);
			result = customerService.queryCustomerList(request);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(null ==result){
			System.out.println("------ query result is empty.");
		}else{
			System.out.println("------ query result:" + JSONObject.toJSONString(result));
		}
	}

	private static <T> T getDubboService(Class clazz, String version) throws ClassNotFoundException {
		ApplicationConfig application = new ApplicationConfig();
		application.setName(APPLICATION_NAME);
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress(TEST_ZK_URL);
		ReferenceConfig<T> rc = new ReferenceConfig<T>();
		rc.setApplication(application);
		rc.setRegistry(registry);
		rc.setInterface(clazz.getName());
		rc.setUrl("dubbo://127.0.0.1:12345/com.example.mq.web.CustomerServiceApi");
		if (!StringUtils.isEmpty(version)) {
			rc.setVersion(version);
		}
		rc.setProtocol("dubbo");
		rc.setTimeout(3000);
		return rc.get();
	}
}
