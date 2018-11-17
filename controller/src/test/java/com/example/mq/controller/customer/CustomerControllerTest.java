package com.example.mq.controller.customer;

import com.example.mq.controller.ControllerApplication;
import com.example.mq.controller.api.CustomerController;
import com.example.mq.controller.bean.CustomerDTO;
import com.example.mq.data.common.Response;
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
            resp =customerController.queryByCustomerId(123456L);
        } catch (Exception e) {
            LOG.error("query customer err!", e);
        }
        Assert.assertTrue(!Objects.isNull(resp) && !Objects.isNull(resp.getData()));
        CustomerDTO dto =(CustomerDTO) resp.getData();
        Assert.assertTrue(!Objects.isNull(dto) && dto.getCustomerId().equals("111"));
    }

    @Test
    public void insert() {
    }
}