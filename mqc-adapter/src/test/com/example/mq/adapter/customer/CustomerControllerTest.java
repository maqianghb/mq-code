package com.example.mq.adapter.customer;

import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.client.customer.request.CustomerRequest;
import com.example.mq.common.base.MqcResponse;
import com.example.mq.domain.customer.CustomerDomainService;
import com.example.mq.domain.customer.model.CustomerEntity;
import org.apache.commons.collections4.CollectionUtils;
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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 15:19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerControllerTest {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerControllerTest.class);

    @MockBean
	private CustomerDomainService customerDomainService;

    @Autowired
    private CustomerController customerController;

	@Before
	public void setUp() throws Exception {
        CustomerEntity mockCustomer =new CustomerEntity();
		mockCustomer.setCustomerName("mockName");
        List<CustomerEntity> mockCustomerList = Arrays.asList(mockCustomer);
		Mockito.when(customerDomainService.queryCustomerList(Mockito.any(CustomerEntity.class))).thenReturn(mockCustomerList);
	}

	@Test
    public void queryByCustomerList() {
        CustomerRequest request =new CustomerRequest();
        request.setCustomerNo("123456");
        MqcResponse<List<CustomerDTO>> response = customerController.queryCustomerList(request);

        Assert.assertTrue(null != response && CollectionUtils.isNotEmpty(response.getData()));

        CustomerDTO customerDTO = response.getData().get(0);
        Assert.assertTrue(null !=customerDTO && "mockName".equals(customerDTO.getCustomerName()));
    }


    @Test
    public void queryAll() throws Exception{
        CustomerControllerTest test =new CustomerControllerTest();
        test.testParseNumber();
    }

    private void testParseNumber() throws Exception{
        String numStr ="100,928,185.37";
        double result = new DecimalFormat().parse(numStr).doubleValue();
        System.out.println("result:"+result);
    }

    @Test
    public void insert() {
    }

}