package com.example.mq.client.service.customer;

import java.util.List;

import com.example.mq.client.common.Result;
import com.example.mq.client.service.customer.dto.CustomerDTO;
import com.example.mq.client.service.customer.request.CustomerRequest;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 00:48
 */
public interface CustomerService {

    Result<List<CustomerDTO>> queryCustomerByPage(CustomerRequest customerRequest);

	Result<List<CustomerDTO>> queryCustomerList(CustomerRequest customerRequest);

    Result<Boolean> addCustomer(CustomerRequest customerRequest);

    Result<Boolean> batchAddCustomer(CustomerRequest customerRequest);
    
}
