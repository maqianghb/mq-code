package com.example.mq.service.customer;

import com.example.mq.service.bean.Customer;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/3
 *
 */

public interface CustomerSaveService {

	long saveCustomer(Customer customer) throws Exception;

}
