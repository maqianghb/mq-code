package com.example.mq.service.dubbo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.service.bean.dubbo.DubboRegisterConfig;
import com.example.mq.service.bean.dubbo.DubboRequestConfig;
import com.example.mq.service.config.ConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */
@Component
public class DubboReferenceCache {
	private static Logger LOG = LoggerFactory.getLogger(DubboReferenceCache.class);

	private static final String APPLICATION_NAME ="mq_code";
	private ApplicationConfig applicationConfig =null;

	/**
	 * 注：需要手动更新cache，所以暂不使用<ReferenceConfigCache>类；
	 * dubbo接口的ReferenceConfig缓存
	 * map(interfaceName, ReferenceConfig)
	 */
	private static ConcurrentHashMap<String, ReferenceConfig> referenceCache = new ConcurrentHashMap<>();

	private long lastUpdateTime =-1;

	@Autowired
	private ConfigService configService;


	public long updateCache(){
		//update cache
		if(this.doUpdateAllCache() <=0){
			LOG.error(" updateCache err.");
			return 0;
		}

		//if success
		lastUpdateTime =System.currentTimeMillis();
		return 1;
	}

	private long doUpdateAllCache(){
		//get dubboRequestNameList
		List<String> requestNameList = new ArrayList<>();
		if(CollectionUtils.isEmpty(requestNameList)){
			LOG.warn(" requestNameList is empty!");
			return 1;
		}

		//update cache
		int result =1;
		for(String requestName : requestNameList){
			try {
				if(this.updateCache(requestName) <=0){
					LOG.error(" update reference cache err, requestName:{}", requestName);
					result =0;
				}
			} catch (Exception e) {
				LOG.error(" update reference cache err, requestName:{}", requestName, e);
				result =0;
			}
		}
		return result;
	}

	public ReferenceConfig getReferenceConfig(String interfaceName) throws Exception {
		if (StringUtils.isEmpty(interfaceName)) {
			throw new IllegalArgumentException(" getReferenceConfig 操作，参数为空！");
		}
		ReferenceConfig config = referenceCache.get(interfaceName);
		if (null == config) {
			this.updateCache();
			config =referenceCache.get(interfaceName);
		}
		if(null ==config){
			LOG.error(" referenceConfig is empty, interfaceName:{}", interfaceName);
		}
		return config;
	}

	private long updateCache(String requestName) throws Exception{
		if(StringUtils.isEmpty(requestName)){
			throw new IllegalArgumentException(" updateCache 操作，参数为空！");
		}
		// feature config
		DubboRegisterConfig dubboRegisterConfig =configService.getRegisterConfig(requestName);
		DubboRequestConfig dubboRequestConfig =configService.getRequestConfig(requestName);
		if(null ==dubboRegisterConfig || null ==dubboRequestConfig){
			LOG.error(" dubboRegisterConfig or dubboRequestConfig is null!");
			return 0;
		}

		//interfaceClass
		String interfaceName =dubboRequestConfig.getInterfaceName();
		if(StringUtils.isEmpty(interfaceName)){
			LOG.error(" interfaceName is empty, dubboRequestConfig:{}", JSONObject.toJSONString(dubboRequestConfig));
			return 0;
		}
		Class interfaceClass = null;
		try {
			interfaceClass = Class.forName(interfaceName);
		} catch (ClassNotFoundException e) {
			LOG.error(" find class err, interfaceName:{}", interfaceName, e);
			return 0;
		}

		// reference config
		ReferenceConfig referenceConfig =new ReferenceConfig();
		referenceConfig.setApplication(this.getApplicationConfig());
		referenceConfig.setRegistry(DubboRegisterConfig.convertToRegisterConfig(dubboRegisterConfig));
		referenceConfig.setProtocol(dubboRequestConfig.getProtocol());
		referenceConfig.setInterface(interfaceClass);
		if(!StringUtils.isEmpty(dubboRequestConfig.getVersion())){
			referenceConfig.setVersion(dubboRequestConfig.getVersion());
		}
		referenceConfig.setRetries(dubboRequestConfig.getRetries());
		referenceConfig.setTimeout(dubboRequestConfig.getTimeout());
		referenceConfig.setGeneric(true);

		referenceCache.put(interfaceName, referenceConfig);
		return 1;
	}

	private ApplicationConfig getApplicationConfig(){
		if(null ==this.applicationConfig){
			ApplicationConfig applicationConfig =new ApplicationConfig();
			applicationConfig.setName(APPLICATION_NAME);
			this.applicationConfig =applicationConfig;
		}
		return this.applicationConfig;
	}
}
