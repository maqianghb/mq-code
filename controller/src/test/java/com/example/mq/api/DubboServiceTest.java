package com.example.mq.api;


import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.fastjson.JSONObject;

import com.example.mq.api.dto.common.Response;
import com.example.mq.api.dto.request.CustomerRequestDTO;
import com.example.mq.service.bean.Customer;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/8
 *
 */

public class DubboServiceTest {
	private static Logger LOG = LoggerFactory.getLogger(DubboServiceTest.class);

	private static final String APPLICATION_NAME ="test_application";
	private static final String DEV_ZK_URL = "127.0.0.1:12345";

	public static void main(String[] args){
		DubboServiceTest dubboServiceTest = new DubboServiceTest();

		dubboServiceTest.testQueryCustomer();
		System.out.println("------test end.");
	}

	private void testQueryCustomer(){
		CustomerRequestDTO vo =new CustomerRequestDTO();
		vo.setCustomerId("123456789");

		Response response =null;
		try {
			CustomerServiceApi customerServiceApi = DubboServiceTest.getDubboService(CustomerServiceApi.class, null);
			response = customerServiceApi.queryCustomer(vo);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(null !=response && response.getCode() ==200){
			Customer customer =(Customer) response.getData();
			System.out.println("------result:" + JSONObject.toJSONString(customer));
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
