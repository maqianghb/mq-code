package com.example.mq.service.customer.impl;

import com.example.mq.core.domain.customer.model.Customer;
import com.example.mq.core.manager.customer.CustomerManager;
import com.example.mq.service.customer.CustomerDomainService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 23:09
 */
@Component
@Slf4j
public class CustomerDomainServiceImpl implements CustomerDomainService {

    @Resource
    private CustomerManager customerManager;

	@Override
	public Customer countCustomer(Customer customer) {
		return null;
	}

	@Override
	public List<Customer> queryCustomerByPage(Customer customer) {
		return null;
	}

	@Override
	public List<Customer> queryCustomerList(Customer customer) {
		return null;
	}
}
