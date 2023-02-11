package com.example.mq.service.dubboservice;

import com.example.mq.client.common.Result;
import com.example.mq.client.service.customer.CustomerService;
import com.example.mq.client.service.customer.dto.CustomerDTO;
import com.example.mq.client.service.customer.request.CustomerRequest;
import com.example.mq.core.domain.customer.model.Customer;
import com.example.mq.service.annotation.HotDataSourceSelector;
import com.example.mq.service.customer.CustomerDomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Resource
    private CustomerDomainService customerDomainService;

    @HotDataSourceSelector()
    @Override
    public Result<List<CustomerDTO>> queryCustomerByPage(CustomerRequest customerRequest) {
        Asserts.notNull(customerRequest, "customerRequest is null.");

        Long customerId =Long.parseLong(customerRequest.getId());
        Customer customer = null;
        try {
            List<Customer> customerList = customerDomainService.queryCustomerByPage(new Customer());
            customer = Optional.of(customerList).orElse(Lists.newArrayList()).stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.error("customerService queryById err, customerId:{}", customerId);
            return Result.fail("queryCustomer fail.");
        }
        if(customer ==null){
            log.warn(" customer is null, customerId:{}", customerId);
            return Result.success();
        }
        CustomerDTO customerDTO =null;
        return Result.success(Arrays.asList(customerDTO));
    }

    @Override
    public Result<List<CustomerDTO>> queryCustomerList(CustomerRequest customerRequest) {
        return null;
    }

    @Override
    public Result<Boolean> addCustomer(CustomerRequest customerRequest) {
        return null;
    }

    @Override
    public Result<Boolean> batchAddCustomer(CustomerRequest customerRequest) {
        return null;
    }

}
