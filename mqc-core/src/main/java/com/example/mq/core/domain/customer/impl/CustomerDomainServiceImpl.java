package com.example.mq.core.domain.customer.impl;

import com.example.mq.core.domain.customer.CustomerDomainService;
import com.example.mq.core.domain.customer.model.Customer;
import com.example.mq.core.manager.customer.CustomerManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class CustomerDomainServiceImpl implements CustomerDomainService {

    @Resource
    private CustomerManager customerManager;

    @Override
    public List<Customer> queryCustomerList(Customer customer) {
        return null;
    }
}
