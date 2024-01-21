package com.example.mq.service.customer;


import com.example.mq.client.service.customer.dto.CustomerDTO;
import com.example.mq.core.domain.customer.model.Customer;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/3
 *
 */

public interface CustomerSaveService {

	CustomerDTO queryCustomer(CustomerDTO customerDTO);

	long saveCustomer(CustomerDTO customerDTO) throws Exception;

}
