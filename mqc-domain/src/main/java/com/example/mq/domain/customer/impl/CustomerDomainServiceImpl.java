package com.example.mq.domain.customer.impl;

import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.domain.customer.CustomerDomainService;
import com.example.mq.domain.customer.model.CustomerEntity;
import com.example.mq.infra.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class CustomerDomainServiceImpl implements CustomerDomainService {

    @Resource
    private CustomerRepository customerRepository;

    @Override
    public List<CustomerEntity> queryCustomerByPage(CustomerEntity customerEntity) {
        return null;
    }

    @Override
    public List<CustomerEntity> queryCustomerList(CustomerEntity customerEntity) {
        return null;
    }

    @Override
    public CustomerEntity countCustomer(CustomerEntity customerEntity) {
        return null;
    }

    @Override
    public Long saveCustomer(CustomerEntity customerEntity) {
        AssertUtils.assertNotNull(customerEntity, BizErrorEnum.PARAM_INVALID);

        return null;
    }
}
