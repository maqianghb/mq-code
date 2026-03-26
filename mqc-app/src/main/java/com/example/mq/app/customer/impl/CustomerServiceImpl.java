package com.example.mq.app.customer.impl;

import com.example.mq.app.customer.CustomerService;
import com.example.mq.app.customer.convertor.CustomerConvertor;
import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.client.customer.request.CustomerRequest;
import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.domain.customer.CustomerDomainService;
import com.example.mq.domain.customer.model.CustomerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-26 11:09:29
 * @Description:
 */
@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerDomainService customerDomainService;

    @Override
    public CustomerDTO queryByCustomerNo(CustomerRequest request) {
        AssertUtils.assertNotNull(request, BizErrorEnum.PARAM_INVALID);
        AssertUtils.assertNotNull(request.getCustomerNo(), BizErrorEnum.PARAM_INVALID);

        return null;
    }

    @Override
    public long saveCustomer(CustomerDTO customerDTO) {
        AssertUtils.assertNotNull(customerDTO, BizErrorEnum.PARAM_INVALID);

        CustomerEntity customerEntity = CustomerConvertor.INSTANCE.mapToCustomerEntity(customerDTO);
        return customerDomainService.saveCustomer(customerEntity);
    }

}
