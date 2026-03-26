package com.example.mq.domain.customer;

import com.example.mq.domain.customer.model.CustomerEntity;

import java.util.List;

public interface CustomerDomainService {

    List<CustomerEntity> queryCustomerByPage(CustomerEntity customerEntity);

    List<CustomerEntity> queryCustomerList(CustomerEntity customerEntity);

    CustomerEntity countCustomer(CustomerEntity customerEntity);

    Long saveCustomer(CustomerEntity customerEntity);

}
