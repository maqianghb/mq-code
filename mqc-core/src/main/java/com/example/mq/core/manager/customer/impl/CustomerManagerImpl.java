package com.example.mq.core.manager.customer.impl;

import com.example.mq.core.manager.customer.CustomerManager;
import com.example.mq.data.mapper.customer.model.CustomerDO;
import com.example.mq.data.mapper.customer.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class CustomerManagerImpl implements CustomerManager {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    public List<CustomerDO> queryCustomerList(CustomerDO customerDO) {
        customerMapper.queryCustomerList(customerDO);
        return null;
    }
}
