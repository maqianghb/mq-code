package com.example.mq.infr.customer.repository;

import com.example.mq.infr.customer.condition.CustomerQueryCondition;
import com.example.mq.infr.customer.model.CustomerDO;

import java.util.List;

public interface CustomerRepository {

    List<CustomerDO> queryCustomerList(CustomerQueryCondition condition);

    void saveCustomerDO(CustomerDO customerDO);

}
