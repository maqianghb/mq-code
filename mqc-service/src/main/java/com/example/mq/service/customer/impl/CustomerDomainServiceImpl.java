package com.example.mq.service.customer.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.base.common.PageResult;
import com.example.mq.base.common.User;
import com.example.mq.base.constant.CustomerConstant;
import com.example.mq.base.enums.PlatformOperateEnum;
import com.example.mq.base.util.DateUtil;
import com.example.mq.common.util.MD5Util;
import com.example.mq.service.bean.Customer;
import com.example.mq.base.common.MyException;
import com.example.mq.service.bean.CustomerOperation;
import com.example.mq.service.bean.CustomerQueryCondition;
import com.example.mq.service.customer.CustomerDomainService;
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
import java.util.Date;
import java.util.List;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018-10-12 23:09
 */
@Service
public class CustomerDomainServiceImpl implements CustomerDomainService {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerDomainServiceImpl.class);


    @Autowired
    private PlatformCustomerMapper platformCustomerMapper;

    @Autowired
	private PlatformCustomerOperateMapper platformCustomerOperateMapper;

	@Override
	public Customer queryById(long id) throws Exception {
		return platformCustomerMapper.selectById(id);
	}

	@Override
    public Customer queryByCustomerNo(long customerNo) throws Exception {
    	return platformCustomerMapper.selectByCustomerNo(customerNo);
    }

	@Override
	public List<Customer> queryAll(CustomerQueryCondition condition) throws Exception {
		return platformCustomerMapper.selectByCondition(condition);
	}

	@Override
    public PageResult<Customer> pageQuery(CustomerQueryCondition condition, int pageNum, int pageSize) throws Exception {
		Page page = PageHelper.startPage(pageNum, pageSize, true);
        List<Customer> customers =platformCustomerMapper.selectByCondition(condition);
		if(CollectionUtils.isEmpty(customers)){
			LOG.warn("未查询到符合条件的顾客数据，condition:{}|pageNum:{}|pageSize:{}",
					JSONObject.toJSONString(condition), pageNum, pageSize);
			return  new PageResult<>(pageNum, pageSize, 0, new ArrayList<>());
		}
		//获取条件中的seller列表
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
        if(null ==customer || null ==customer.getCustomerNo()){
            throw new MyException(-1, "参数为空！");
        }
        //check customer
		if(!this.checkAndPrepareOperate(customer)){
			throw new MyException("customer信息校验不通过！");
		}
		if(null !=platformCustomerMapper.selectByCustomerNo(customer.getCustomerNo())){
			throw new MyException("已存在相同customerNo的顾客信息, customerNo:"+customer.getCustomerNo());
		}

		//save
        customer.setCreateUser(null ==user ? "" : user.getUserName());
        customer.setCreateTime(new Date());
        customer.setMd5(this.createMD5(customer));
		if(platformCustomerMapper.insert(customer) <=0){
			LOG.error("platformCustomerMapper insert err, customer:{}", JSONObject.toJSONString(customer));
			return 0;
		}
		this.saveCustomerOperation(customer, PlatformOperateEnum.ADD.getCode(), user);
        return 1;
    }

	@Transactional(value = "customerTransactionManager", propagation = Propagation.REQUIRED,
			readOnly = false, rollbackFor = { Exception.class})
    @Override
    public long updateById(Customer customer, User user) throws Exception {
        if(null ==customer || null ==customer.getId()){
            throw new MyException("updateById 操作，参数为空！");
        }
		//check customer
		if(!this.checkAndPrepareOperate(customer)){
			throw new MyException("customer信息校验不通过！");
		}
        if(null ==this.queryById(customer.getId())){
            throw new MyException("未找到对应主键id的数据, id:" + customer.getId());
        }

        //update
		customer.setCustomerDesc(customer.getCustomerName()+ DateUtil.formatDateTime(new Date()));
		customer.setUpdateUser(null ==user ? "" : user.getUserName());
		customer.setUpdateTime(new Date());
		customer.setMd5(this.createMD5(customer));
		if(platformCustomerMapper.updateById(customer) <=0){
			LOG.error("platformCustomerMapper updateById err, customer:{}", JSONObject.toJSONString(customer));
			return 0;
		}
		this.saveCustomerOperation(customer, PlatformOperateEnum.UPDATE.getCode(), user);
        return 1;
    }

	@Transactional(value = "customerTransactionManager", propagation = Propagation.REQUIRED,
			readOnly = false, rollbackFor = { Exception.class})
    @Override
    public long deleteById(long id, User user) throws Exception {
		if(null ==this.queryById(id)){
			throw new MyException("未找到对应主键id的数据, id:" + id);
		}
		if(platformCustomerMapper.deleteById(id) <=0){
			LOG.error("platformCustomerMapper delete err, id:{}", id);
			return 0;
		}
		//保存操作明细
		Customer customer =new Customer();
		customer.setId(id);
		this.saveCustomerOperation(customer, PlatformOperateEnum.DELETE.getCode(), user);
        return 1;
    }

    private boolean checkAndPrepareOperate(Customer customer) throws Exception{
    	if(null ==customer){
    		throw new IllegalArgumentException("checkAndPrepareOperate 操作，参数为空！");
		}
		//cheak customerNo
		if(null == customer.getCustomerNo()) {
			return false;
		}
		if(StringUtils.isEmpty(customer.getCustomerName())
				|| !customer.getCustomerName().matches(CustomerConstant.CUSTOMER_NAME_PATTERN)){
			throw new MyException("customer姓名需符合要求：首字母是英文字母, 且仅由大小写英文字母、数字、下划线构成。");
		}
		return true;
	}

    private String createMD5(Customer customer) throws Exception{
    	if(null ==customer){
			throw new IllegalArgumentException("createMD5 操作，参数为空！");
		}
		return MD5Util.getMD5(JSONObject.toJSONString(customer));
	}

	private int saveCustomerOperation(Customer customer, int operateType, User user) throws Exception{
		if(null ==customer){
			throw new IllegalArgumentException("saveCustomerOperation 操作，参数为空！");
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
