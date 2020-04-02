package com.example.mq.service.customer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.base.zk.CuratorClientManager;
import com.example.mq.service.bean.Customer;
import com.example.mq.service.mapper.customer.PlatformCustomerMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

//	private ConcurrentHashMap<Long, Customer> customerCache =new ConcurrentHashMap<>();
	private final static LoadingCache<String, Customer> customerCache = CacheBuilder.newBuilder()
		.initialCapacity(10 *1000)
		.maximumSize(100 *1000)
		.expireAfterWrite(5, TimeUnit.MINUTES)
		.concurrencyLevel(Runtime.getRuntime().availableProcessors() +1)
		.build(new CacheLoader<String, Customer>() {
			@Override
			public Customer load(String key) throws Exception {

				return null;
			}
		});
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

	public Customer getCustomer(){
		return null;
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
		synchronized (CustomerCache.class){
			try {
				customerCache.cleanUp();
				if( !CollectionUtils.isEmpty(customerList)){
					for(Customer customer :customerList){
						customerCache.put(String.valueOf(customer.getCustomerNo()), customer);
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
