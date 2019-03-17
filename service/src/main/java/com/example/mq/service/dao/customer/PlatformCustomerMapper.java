package com.example.mq.service.dao.customer;

import com.example.mq.service.bean.Customer;
import com.example.mq.service.bean.CustomerQueryCondition;

import java.util.List;

/**
 * @program: mq-code
 * @description: customer映射接口
 * @author: maqiang
 * @create: 2018/9/19
 *
 */
public interface PlatformCustomerMapper {

	Customer selectById(long id) throws Exception;

	Customer selectByCustomerNo(long customerNo) throws Exception;

	List<Customer> selectAll() throws Exception;

	List<Customer> selectByCondition(CustomerQueryCondition condition) throws Exception;

	long insert(Customer customer) throws Exception;

	long batchInsert(List<Customer> customers) throws Exception;

	/**
	 * 更新通过主键id操作，避免customer内其他属性修改后无法匹配到记录
	 * @param customer
	 * @return
	 * @throws Exception
	 */
	long updateById(Customer customer) throws Exception;

	long deleteById(long customerNo) throws Exception;
}
