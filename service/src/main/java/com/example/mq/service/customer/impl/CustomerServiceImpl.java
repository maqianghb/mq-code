package com.example.mq.service.customer.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.data.common.PageResult;
import com.example.mq.data.common.User;
import com.example.mq.data.enums.PlatformOperateEnum;
import com.example.mq.data.util.DateUtil;
import com.example.mq.data.util.MD5Util;
import com.example.mq.service.bean.Customer;
import com.example.mq.data.common.MyException;
import com.example.mq.service.bean.CustomerOperation;
import com.example.mq.service.bean.CustomerQueryCondition;
import com.example.mq.service.customer.CustomerService;
import com.example.mq.service.dao.customer.PlatformCustomerMapper;
import com.example.mq.service.dao.customer.PlatformCustomerOperateMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    @Autowired
	private PlatformCustomerOperateMapper platformCustomerOperateMapper;

    @Override
    public Customer queryByCustomerNo(long customerNo) throws Exception {
//    	return platformCustomerMapper.selectByCustomerNo(customerNo);
        Customer customer =new Customer();
        customer.setCustomerNo(customerNo);
        customer.setCustomerName("testCustomerName");
        customer.setTopTenSellers(JSONObject.toJSONString(Arrays.asList("seller1", "seller2", "seller3")));
        customer.setTotalCostAmount(12345);
        return customer;
    }

    @Override
    public PageResult<Customer> pageQuery(CustomerQueryCondition condition, int pageNum, int pageSize) throws Exception {
		Page page = PageHelper.startPage(pageNum, pageSize, true);
//        List<Customer> customers =platformCustomerMapper.selectByCondition(condition);
		List<Customer> customers =new ArrayList<>();
		if(CollectionUtils.isEmpty(customers)){
			LOG.warn("未查询到符合条件的顾客数据，condition:{}|pageNum:{}|pageSize:{}",
					JSONObject.toJSONString(condition), pageNum, pageSize);
			return  new PageResult<>(page.getPageNum(), page.getPageSize(), page.getTotal(), customers);
		}
		List<String> conditionSellers =null;
		if(StringUtils.isEmpty(condition.getTopTenSellers())
				|| CollectionUtils.isEmpty(conditionSellers =JSONObject.parseArray(condition.getTopTenSellers(), String.class))){
			return  new PageResult<>(page.getPageNum(), page.getPageSize(), page.getTotal(), customers);
		}
		//根据条件中的seller过滤
		List<Customer> results =new ArrayList<>(customers.size());
		for(Customer customer : customers){
			List<String> customerSellers = null;
			if(StringUtils.isEmpty(customer.getTopTenSellers()) ||
					CollectionUtils.isEmpty(customerSellers =JSONObject.parseArray(customer.getTopTenSellers(), String.class))){
				continue;
			}
			customerSellers.retainAll(conditionSellers);
			if(!CollectionUtils.isEmpty(customerSellers)){
				results.add(customer);
			}
		}
        return new PageResult<>(page.getPageNum(), page.getPageSize(), page.getTotal(), results);
    }

    //mysql事务中跨库事务是大难题，customer/seller库有各自的事务bean，指定正确的事务bean
    @Transactional(value = "customerTransactionManager", propagation = Propagation.REQUIRED,
			readOnly = false, rollbackFor = { Exception.class})
    @Override
    public long add(Customer customer, User user) throws Exception {
        if(Objects.isNull(customer)){
            throw new MyException(-1, "参数为空！");
        }
        customer.setCreateUser(null ==user ? "" : user.getUserName());
        customer.setCreateTime(new Date());
        customer.setMd5(this.createMD5(customer));
//        long addResult =platformCustomerMapper.insert(customer);
		long addResult =1;
		if(addResult <=0){
			LOG.error("platformCustomerMapper insert err, customer:{}", JSONObject.toJSONString(customer));
			return 0;
		}
		this.saveCustomerOperation(customer, PlatformOperateEnum.ADD.getCode(), user);
        return addResult;
    }

	@Transactional(value = "customerTransactionManager", propagation = Propagation.REQUIRED,
			readOnly = false, rollbackFor = { Exception.class})
    @Override
    public long updateByCustomerNo(Customer customer, User user) throws Exception {
        if(Objects.isNull(customer)){
            throw new MyException(-1, "参数为空！");
        }
        if(null ==this.queryByCustomerNo(customer.getCustomerNo())){
            throw new MyException(-1, "未找到对应customerId的数据！");
        }
		customer.setCustomerDesc(customer.getCustomerName()+ DateUtil.formatDateTime(new Date()));
		customer.setCreateUser(null ==user ? "" : user.getUserName());
		customer.setCreateTime(new Date());
		customer.setMd5(this.createMD5(customer));
//        long updateResult =platformCustomerMapper.updateByCustomerNo(customer);
		long updateResult =1;
		if(updateResult <=0){
			LOG.error("platformCustomerMapper updateByCustomerNo err, customer:{}", JSONObject.toJSONString(customer));
			return 0;
		}
		this.saveCustomerOperation(customer, PlatformOperateEnum.UPDATE.getCode(), user);
        return updateResult;
    }

	@Transactional(value = "customerTransactionManager", propagation = Propagation.REQUIRED,
			readOnly = false, rollbackFor = { Exception.class})
    @Override
    public long deleteByCustomerNo(long customerNo, User user) throws Exception {
//        long delResult =platformCustomerMapper.deleteByCustomerNo(CustomerNo);
		long delResult =1;
		if(delResult <=0){
			LOG.error("platformCustomerMapper delete err, customerNo:{}", customerNo);
			return 0;
		}
		//保存操作明细
		Customer customer =new Customer();
		customer.setCustomerNo(customerNo);
		this.saveCustomerOperation(customer, PlatformOperateEnum.DELETE.getCode(), user);
        return delResult;
    }

    private String createMD5(Customer customer) throws Exception{
    	if(null ==customer){
			throw new IllegalArgumentException("参数为空！");
		}
		return MD5Util.getMD5(JSONObject.toJSONString(customer));
	}

	private int saveCustomerOperation(Customer customer, int operateType, User user) throws Exception{
		if(null ==customer){
			throw new IllegalArgumentException("参数为空！");
		}
		CustomerOperation operation =new CustomerOperation();
		operation.setCustomerNo(customer.getCustomerNo());
		operation.setCustomerName(customer.getCustomerName());
		operation.setMd5(customer.getMd5());

		operation.setOperateType(operateType);
		PlatformOperateEnum operateEnum =PlatformOperateEnum.getByCode(operateType);
		operation.setOperateTypeDesc(null == operateEnum ? "" : operateEnum.getDesc());
		operation.setContent(JSONObject.toJSONString(customer));
		operation.setOperator(null ==user ? "" : user.getUserName());
		operation.setCreateTime(new Date());
		if(platformCustomerOperateMapper.insert(operation) <=0){
			LOG.error("事件操作记录保存失败！, operation:{}", JSONObject.toJSONString(operation));
			return 0;
		}
		return 1;
	}
}
