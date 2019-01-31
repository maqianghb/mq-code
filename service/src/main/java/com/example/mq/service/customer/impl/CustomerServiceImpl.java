package com.example.mq.service.customer.impl;

import com.example.mq.data.common.PageResult;
import com.example.mq.service.bean.Customer;
import com.example.mq.data.common.MyException;
import com.example.mq.service.customer.CustomerService;
import com.example.mq.service.dao.customer.PlatformCustomerMapper;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 23:09
 */
@Service("customerService")
public class CustomerServiceImpl implements CustomerService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private PlatformCustomerMapper platformCustomerMapper;

    @Override
    public Customer queryByCustomerId(Long customerId) throws Exception {
        if(StringUtils.isEmpty(customerId)){
            throw new MyException(-1, "参数为空！");
        }
//        return platformCustomerMapper.selectByCustomerId(customerId);
        Customer customer =new Customer();
        customer.setCustomerId(customerId);
        customer.setName("testName");
        return customer;
    }

    @Override
    public PageResult<Customer> pageQuery(Integer pageNum, Integer pageSize) throws Exception {
        if(Objects.isNull(pageNum) ||Objects.isNull(pageSize)){
            throw new MyException(-1, "参数为空！");
        }
        if(pageNum <=0 || pageSize <=0){
            throw new MyException(-1, "参数不合法！");
        }
        PageHelper.startPage(pageNum, pageSize);
//        List<Customer> customers =platformCustomerMapper.selectAll();
		List<Customer> customers =new ArrayList<>();
        return new PageResult<>(pageNum, pageSize, customers.size(), customers);
    }

    @Override
    public List<Customer> queryAll() throws Exception {
//        return platformCustomerMapper.selectAll();
		List<Customer> customers =new ArrayList<>();
		return customers;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class})
    @Override
    public Integer insert(Customer customer) throws Exception {
        if(Objects.isNull(customer)){
            throw new MyException(-1, "参数为空！");
        }
//        int saveValue =platformCustomerMapper.insert(customer);
		int value =1;
        //TODO 保存至操作明细
        return value;
    }


    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class})
    @Override
    public Integer updateByCustomerId(Customer customer) throws Exception {
        if(Objects.isNull(customer)){
            throw new MyException(-1, "参数为空！");
        }
        Customer oldCustomer =this.queryByCustomerId(customer.getCustomerId());
        if(Objects.isNull(oldCustomer)){
            throw new MyException(-1, "未找到对应customerId的数据！");
        }
//        int saveValue =platformCustomerMapper.update(customer);
		int value =1;
        //TODO 保存至操作明细
        return value;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class})
    @Override
    public Integer deleteByCustomerId(Long CustomerId) throws Exception {
        if(StringUtils.isEmpty(CustomerId)){
            throw new MyException(-1, "参数为空！");
        }
//        int saveValue =platformCustomerMapper.deleteByCustomerId(CustomerId);
		int value =1;
        //TODO 保存至操作明细
        return value;
    }
}
