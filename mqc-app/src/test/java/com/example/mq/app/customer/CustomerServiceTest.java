package com.example.mq.app.customer;

import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.client.customer.request.CustomerRequest;
import com.example.mq.domain.customer.CustomerDomainService;
import com.example.mq.domain.customer.model.CustomerEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/9/17
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CustomerServiceTest {

//	@MockBean
	@SpyBean
	private CustomerDomainService customerDomainService;

	@Autowired
	private CustomerService customerService;

	@Before
	public void setUp() throws Exception {
		CustomerEntity mockCustomer =new CustomerEntity();
		mockCustomer.setCustomerName("mockName");
		List<CustomerEntity> customerEntityList =new ArrayList<>();
		customerEntityList.add(mockCustomer);
		Mockito.when(customerDomainService.queryCustomerList(Mockito.any())).thenReturn(customerEntityList);
	}

	@Test
	public void queryByCustomerNo() {
		long customerNo =123456L;
		CustomerRequest request = new CustomerRequest();
		CustomerDTO customerDTO = customerService.queryByCustomerNo(request);
		Assert.assertTrue( null !=customerDTO && customerDTO.getCustomerName() =="mockName");
	}

}