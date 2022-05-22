package com.example.mq.api;

import java.util.List;

import com.example.mq.api.dto.common.Response;
import com.example.mq.api.dto.response.CustomerDTO;
import com.example.mq.api.dto.request.CustomerRequestDTO;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-13 00:48
 */
public interface CustomerServiceApi {

    Response<CustomerDTO> queryCustomer(CustomerRequestDTO requestDTO);

	Response<List<CustomerDTO>> batchQueryCustomer(CustomerRequestDTO requestDTO);
}
