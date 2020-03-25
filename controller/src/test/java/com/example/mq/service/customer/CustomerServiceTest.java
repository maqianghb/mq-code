package com.example.mq.service.customer;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.mq.controller.ControllerApplication;
import com.example.mq.controller.customer.CustomerControllerTest;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.dao.customer.PlatformCustomerMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/9/17
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ControllerApplication.class)
public class CustomerServiceTest {
	private static final Logger LOG = LoggerFactory.getLogger(CustomerControllerTest.class);

//	@MockBean
	@SpyBean
	private PlatformCustomerMapper platformCustomerMapper;

	@Autowired
	private CustomerService customerService;

	@Before
	public void setUp() throws Exception {
		Customer mockCustomer =new Customer();
		mockCustomer.setCustomerName("mockName");
		Mockito.when(platformCustomerMapper.selectByCustomerNo(Mockito.anyLong())).thenReturn(mockCustomer);

	}

	@Test
	public void queryByCustomerNo() {
		long customerNo =123456L;
		Customer customer = null;
		try {
			customer = customerService.queryByCustomerNo(customerNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertTrue( null !=customer && customer.getCustomerName() =="mockName");
	}
}