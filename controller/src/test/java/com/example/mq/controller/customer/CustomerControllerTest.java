package com.example.mq.controller.customer;

import com.example.mq.api.common.Response;
import com.example.mq.controller.ControllerApplication;
import com.example.mq.controller.api.CustomerController;
import com.example.mq.service.bean.Customer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Autowired
    private CustomerController customerController;

    @Test
    public void queryByCustomerId() {
        Response resp =null;
        try {
            resp =customerController.queryByCustomerId("111");
        } catch (Exception e) {
            LOG.error("query customer err!", e);
        }
        Assert.assertTrue(!Objects.isNull(resp) && !Objects.isNull(resp.getData()));
        Customer customer =(Customer) resp.getData();
        Assert.assertTrue(!Objects.isNull(customer) && customer.getCustomerId().equals("111"));
    }

    @Test
    public void insert() {
    }
}