package com.example.mq.adapter.customer;

import com.example.mq.adapter.annotation.HotDataSourceSelector;
import com.example.mq.common.base.MqcResponse;
import com.example.mq.client.customer.CustomerClient;
import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.client.customer.request.CustomerRequest;
import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.utils.AssertUtils;
import com.example.mq.domain.customer.CustomerDomainService;
import com.example.mq.domain.customer.model.CustomerEntity;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/mqc/code/customer")
@Slf4j
public class CustomerController implements CustomerClient {

    @Resource
    private CustomerDomainService customerDomainService;

    @HotDataSourceSelector()
    @Override
    public MqcResponse<List<CustomerDTO>> queryCustomerByPage(CustomerRequest request) {
        AssertUtils.assertNotNull(request, BizErrorEnum.PARAM_INVALID);

        CustomerEntity customerEntity =new CustomerEntity();
        List<CustomerEntity> customerEntities = customerDomainService.queryCustomerByPage(customerEntity);
        List<CustomerDTO> customerDTOList = Optional.of(customerEntities).orElse(Lists.newArrayList()).stream()
                .map(customerEntity1 -> new CustomerDTO())
                .collect(Collectors.toList());

        return MqcResponse.success(customerDTOList);
    }

    @Override
    public MqcResponse<List<CustomerDTO>> queryCustomerList(CustomerRequest request) {
        AssertUtils.assertNotNull(request, BizErrorEnum.PARAM_INVALID);

        return null;
    }

    @Override
    public MqcResponse<Long> saveCustomer(CustomerRequest request) {
        AssertUtils.assertNotNull(request, BizErrorEnum.PARAM_INVALID);

        return null;
    }

    @Override
    public MqcResponse<Integer> batchSaveCustomer(CustomerRequest request) {
        AssertUtils.assertNotNull(request, BizErrorEnum.PARAM_INVALID);

        return null;
    }

}
