package com.example.mq.controller.customer;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.controller.ControllerApplication;
import com.example.mq.controller.api.CustomerController;
import com.example.mq.controller.bean.CustomerVO;
import com.example.mq.base.common.Response;
import org.apache.commons.collections4.CollectionUtils;
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
    	long customerNo =123456L;
        Response resp =null;
        try {
            resp =customerController.queryByCustomerNo(customerNo);
        } catch (Exception e) {
            LOG.error("query customer err!", e);
        }

        Assert.assertTrue(!Objects.isNull(resp) && !Objects.isNull(resp.getData()));
        CustomerVO vo =(CustomerVO) resp.getData();
		LOG.info("customerVO:{}", JSONObject.toJSONString(vo));
        Assert.assertTrue(!Objects.isNull(vo) && vo.getCustomerNo().equals("123456"));
        Assert.assertTrue(!CollectionUtils.isEmpty(vo.getTopTenSellers()));
        Assert.assertTrue(null !=vo.getTotalCostAmount() && vo.getTotalCostAmount() ==123.45);
    }

    @Test
    public void insert() {
    }
}