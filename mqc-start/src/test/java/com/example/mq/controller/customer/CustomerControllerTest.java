package com.example.mq.controller.customer;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.client.common.Result;
import com.example.mq.controller.ControllerApplication;
import com.example.mq.controller.web.CustomerController;
import com.example.mq.controller.bean.CustomerVO;
import com.example.mq.core.domain.customer.model.Customer;
import com.example.mq.service.customer.CustomerDomainService;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 15:19
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ControllerApplication.class)
public class CustomerControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerControllerTest.class);

    @MockBean
	private CustomerDomainService customerDomainService;

    @Autowired
    private CustomerController customerController;

	@Before
	public void setUp() throws Exception {
        Customer mockCustomer =new Customer();
		mockCustomer.setCustomerName("mockName");
        List<Customer> mockCustomerList = Arrays.asList(mockCustomer);
		Mockito.when(customerDomainService.queryCustomerList(Mockito.any(Customer.class))).thenReturn(mockCustomerList);
	}

	@Test
    public void queryByCustomerId() {
    	long customerNo =123456L;
        Result result =null;
        try {
            result =customerController.queryByCustomerNo(customerNo);
        } catch (Exception e) {
            LOG.error("query customer err!", e);
        }

        Assert.assertTrue(null !=result && null !=result.getData());
        CustomerVO vo =(CustomerVO) result.getData();
		LOG.info("customerVO:{}", JSONObject.toJSONString(vo));
        Assert.assertTrue(null !=vo && "mockName".equals(vo.getCustomerName()));
    }

    @Test
    public void insert() {
    }
}