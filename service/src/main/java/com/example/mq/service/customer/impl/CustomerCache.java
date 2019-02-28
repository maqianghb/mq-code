package com.example.mq.service.customer.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.example.mq.data.zk.CuratorClientManager;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.dao.customer.PlatformCustomerMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @program: mq-code
 * @description: Customer本地缓存
 * @author: maqiang
 * @create: 2019/2/28
 *
 */
@Component
public class CustomerCache {
	private static final Logger LOG = LoggerFactory.getLogger(CustomerCache.class);

	private ConcurrentHashMap<Long, Customer> customerCache =new ConcurrentHashMap<>();

	@Autowired
	private PlatformCustomerMapper platformCustomerMapper;

	@Autowired
	private CuratorClientManager curatorClientManager;

	@PostConstruct
	private void initMixFeature(){
		if(this.loadCustomers() <=0){
			LOG.error("loadCustomer err!");
		}
		//zk订阅customerCache的更新通知
		try {
			curatorClientManager.watchChildrens("/customer/cache", (curatorFramework, pathChildrenCacheEvent) -> {
				this.loadCustomers();
			});
		} catch (Exception e) {
			LOG.error("refer customerCache error",e);
		}
	}

	private int loadCustomers(){
		List<Customer> customers =null;
		try {
			customers = platformCustomerMapper.selectAll();
		} catch (Exception e) {
			LOG.error("platformCustomerMapper selectAll err!", e);
			return 0;
		}
		int result =0;
		synchronized (customerCache){
			try {
				customerCache.clear();
				if( !CollectionUtils.isEmpty(customers)){
					for(Customer customer :customers){
						customerCache.putIfAbsent(customer.getCustomerNo(), customer);
					}
				}
				result =1;
			}catch (Exception e) {
				LOG.error("loadMixFeature error!", e);
			}
		}
		return result;
	}
}
