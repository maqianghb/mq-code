package com.example.mq.infra.customer.repository;

import com.example.mq.infra.customer.condition.CustomerQueryCondition;
import com.example.mq.infra.customer.model.CustomerDO;

import java.util.List;

public interface CustomerRepository {

    List<CustomerDO> queryCustomerList(CustomerQueryCondition condition);

    void saveCustomerDO(CustomerDO customerDO);

}
