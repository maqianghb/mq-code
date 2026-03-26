package com.example.mq.app.customer;


import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.client.customer.request.CustomerRequest;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/3
 *
 */
public interface CustomerService {

	CustomerDTO queryByCustomerNo(CustomerRequest request);

	long saveCustomer(CustomerDTO customerDTO);

}
