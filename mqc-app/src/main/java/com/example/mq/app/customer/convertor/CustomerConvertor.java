package com.example.mq.app.customer.convertor;

import com.example.mq.client.customer.model.CustomerDTO;
import com.example.mq.domain.customer.model.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author: maqiang
 * @CreateTime: 2026-03-26 11:00:36
 * @Description:
 */
@Mapper
public interface CustomerConvertor {
    CustomerConvertor INSTANCE = Mappers.getMapper(CustomerConvertor.class);

    CustomerDTO mapToCustomerDTO(CustomerEntity customerEntity);

    CustomerEntity mapToCustomerEntity(CustomerDTO customerDTO);

}
