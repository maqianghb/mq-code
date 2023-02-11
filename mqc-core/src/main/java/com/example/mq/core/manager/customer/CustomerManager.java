package com.example.mq.core.manager.customer;

import com.example.mq.core.manager.customer.model.CustomerDO;

import java.util.List;

public interface CustomerManager {

    List<CustomerDO> queryCustomerList(CustomerDO customerDO);

}
