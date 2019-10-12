package com.example.mq.controller.customer;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.controller.ControllerApplication;
import com.example.mq.controller.web.CustomerController;
import com.example.mq.controller.bean.CustomerVO;
import com.example.mq.api.common.Response;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.dao.customer.PlatformCustomerMapper;
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

import java.util.Objects;

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
	private PlatformCustomerMapper platformCustomerMapper;

    @Autowired
    private CustomerController customerController;

	@Before
	public void setUp() throws Exception {
		Customer mockCustomer =new Customer();
		mockCustomer.setCustomerName("mockName");
		Mockito.when(platformCustomerMapper.selectByCustomerNo(Mockito.anyLong())).thenReturn(mockCustomer);
	}

	@Test
    public void queryByCustomerId() {
    	long customerNo =123456L;
        Response resp =null;
        try {
            resp =customerController.queryByCustomerNo(customerNo);
        } catch (Exception e) {
            LOG.error("query customer err!", e);
        }

        Assert.assertTrue(null !=resp && null !=resp.getData());
        CustomerVO vo =(CustomerVO) resp.getData();
		LOG.info("customerVO:{}", JSONObject.toJSONString(vo));
        Assert.assertTrue(null !=vo && "mockName".equals(vo.getCustomerName()));
    }

    @Test
    public void insert() {
    }
}