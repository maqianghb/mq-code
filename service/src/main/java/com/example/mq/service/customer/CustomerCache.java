package com.example.mq.service.customer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.base.zk.CuratorClientManager;
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
	private final boolean PRINT_CACHE = true;
	private long LAST_UPDATE_TIME =0;


	@Autowired
	private PlatformCustomerMapper platformCustomerMapper;

	@Autowired
	private CuratorClientManager curatorClientManager;

//	@PostConstruct
	public int initCacheAndRegisteZk(){
		if(this.loadCustomers() <=0){
			LOG.error("loadCustomer err!");
			return 0;
		}
		//zk订阅customerCache的更新通知
		try {
			curatorClientManager.watchChildrens("/customer/cache", (curatorFramework, pathChildrenCacheEvent) -> {
				this.loadCustomers();
			});
		} catch (Exception e) {
			LOG.error("refer customerCache error",e);
			return 0;
		}
		return 1;
	}

	private long loadCustomers(){
		//load customer
		List<Customer> customers =null;
		try {
			customers = platformCustomerMapper.selectAll();
		} catch (Exception e) {
			LOG.error(" platformCustomerMapper selectAll err!", e);
			return 0;
		}
		if(CollectionUtils.isEmpty(customers)){
			LOG.error(" result of platformCustomerMapper selectAll is empty.");
			return 0;
		}

		//update cache
		if(this.updateCacheAndPrint(customers) <=0){
			LOG.error(" updataCacheAndPrint err.");
			return 0;
		}

		//if success
		LAST_UPDATE_TIME =System.currentTimeMillis();
		return 1;
	}

	private long updateCacheAndPrint(List<Customer> customerList){
		int result =0;
		synchronized (customerCache){
			try {
				customerCache.clear();
				if( !CollectionUtils.isEmpty(customerList)){
					for(Customer customer :customerList){
						customerCache.putIfAbsent(customer.getCustomerNo(), customer);
					}
				}
				if(PRINT_CACHE){
					LOG.info("customer cache detail:{}", null ==customerCache ? "" : JSONObject.toJSONString(customerCache));
				}
				result =1;
			}catch (Exception e) {
				LOG.error("update cache error!", e);
			}
		}
		return result;
	}
}
