package com.example.mq.infr.cache;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.example.mq.infr.customer.model.CustomerDO;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;


/**
 * @program: mq-code
 * @description: 本地缓存组件
 * @author: maqiang
 * @create: 2019/2/28
 *
 */
@Component
@Slf4j
public class LocalCacheManager {

	private static final String SPLIT_CHAR ="_";
	private static final String CUSTOMER_CACHE_KEY ="customer_cache:";

	private final static Cache<String, String> localCache = CacheBuilder.newBuilder()
		.initialCapacity(10 *1000)
		.maximumSize(100 *1000)
		.expireAfterWrite(5, TimeUnit.MINUTES)
		.concurrencyLevel(Runtime.getRuntime().availableProcessors() +1)
		.build();


	/**
	 * 客户信息写入本地缓存
	 * @param customerNo
	 * @param customerType
	 * @param customerDO
	 */
	public void putCustomerCache(String customerNo, String customerType, CustomerDO customerDO){
		if(StringUtils.isBlank(customerNo) || StringUtils.isBlank(customerType) || customerDO ==null){
			return;
		}

		String cacheKey = CUSTOMER_CACHE_KEY + customerNo + SPLIT_CHAR + customerType;
		localCache.put(cacheKey, JSON.toJSONString(customerDO));
	}

	/**
	 * 客户信息查询
	 * @param customerNo
	 * @param customerType
	 */
	public CustomerDO getCustomerCache(String customerNo, String customerType){
		if(StringUtils.isBlank(customerNo) || StringUtils.isBlank(customerType)){
			return null;
		}

		String cacheKey = CUSTOMER_CACHE_KEY + customerNo + SPLIT_CHAR + customerType;
		String strCache = localCache.getIfPresent(cacheKey);
		if(StringUtils.isNotBlank(strCache)){
			return JSON.parseObject(strCache, CustomerDO.class);
		}

		return null;
	}

}
