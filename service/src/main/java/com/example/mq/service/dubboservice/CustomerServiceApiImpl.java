package com.example.mq.service.dubboservice;

import com.example.mq.api.CustomerServiceApi;
import com.example.mq.api.dto.common.Response;
import com.example.mq.api.dto.request.CustomerRequestDTO;
import com.example.mq.api.dto.response.CustomerDTO;
import com.example.mq.service.annotation.HotDataSourceSelector;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.customer.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;


@Service
@Slf4j
public class CustomerServiceApiImpl implements CustomerServiceApi {

    @Autowired
    private CustomerService customerService;

    @HotDataSourceSelector()
    @Override
    public Response<CustomerDTO> queryCustomer(CustomerRequestDTO requestDTO) {
        Assert.notNull(requestDTO, "requestDTO is null.");
        Long customerId =Long.parseLong(requestDTO.getCustomerId());
        Customer customer = null;
        try {
            customer = customerService.queryById(customerId);
        } catch (Exception e) {
            log.error("customerService queryById err, customerId:{}", customerId);
            return Response.createByFailMsg("queryCustomer fail.");
        }
        if(customer ==null){
            log.warn(" customer is null, customerId:{}", customerId);
            return Response.createBySuccess();
        }
        CustomerDTO customerDTO =CustomerDTO.builder()
                .customerId(customer.getCustomerNo() != null ? String.valueOf(customer.getCustomerNo()) : "")
                .name(customer.getCustomerName() !=null ? customer.getCustomerName() : "")
                .build();
        return Response.createBySuccess(customerDTO);
    }

    @Override
    public Response<List<CustomerDTO>> batchQueryCustomer(CustomerRequestDTO requestDTO) {
        return null;
    }
}
