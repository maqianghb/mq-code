package com.example.mq.service.dao.customer;

import java.util.List;

import com.example.mq.service.bean.CustomerOperation;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/2/27
 *
 */

public interface PlatformCustomerOperateMapper {

	long insert(CustomerOperation operation) throws Exception;

	List<CustomerOperation> selectByCustomerNo(long customerNo) throws Exception;
}
