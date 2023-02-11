package com.example.mq.service.dubbo.impl;

import java.util.List;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.core.utils.DubboUtils;
import com.example.mq.service.bean.dubbo.DubboRegisterConfig;
import com.example.mq.service.bean.dubbo.DubboRequestConfig;
import com.example.mq.service.dubbo.AutoDubboService;
import com.example.mq.service.dubbo.DubboReferenceCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */
@Service
public class AutoDubboServiceImpl implements AutoDubboService {
	private static Logger LOG = LoggerFactory.getLogger(AutoDubboServiceImpl.class);

	@Autowired
	private DubboReferenceCache dubboReferenceCache;


	@Override
	public Object generalizedExecute(DubboRegisterConfig dubboRegisterConfig, DubboRequestConfig dubboRequestConfig,
			List<Object> paramList) throws Exception {
		if(null ==dubboRegisterConfig || null ==dubboRequestConfig){
			throw new IllegalArgumentException(" generalizedExecute 操作，参数为空！");
		}
		RegistryConfig registryConfig =DubboRegisterConfig.convertToRegisterConfig(dubboRegisterConfig);
		String interfaceName =dubboRequestConfig.getInterfaceName();
		if(StringUtils.isEmpty(interfaceName)){
			LOG.error(" interfaceName is empty, dubboRequestConfig:{}", JSONObject.toJSONString(dubboRequestConfig));
			return null;
		}
		ReferenceConfig referenceConfig =dubboReferenceCache.getReferenceConfig(interfaceName);
		if(null ==registryConfig || null ==referenceConfig){
			LOG.error(" registryConfig or referenceConfig is null, dubboRegisterConfig:{}|dubboRequestConfig:{}",
					JSONObject.toJSONString(dubboRegisterConfig), JSONObject.toJSONString(dubboRequestConfig));
			return null;
		}
		Object resp = DubboUtils.invoke(referenceConfig, dubboRequestConfig.getMethod(), paramList);
		return resp;
	}
}
