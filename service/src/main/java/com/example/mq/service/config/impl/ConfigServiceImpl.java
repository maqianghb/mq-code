package com.example.mq.service.config.impl;

import com.example.mq.service.bean.dubbo.DubboRegisterConfig;
import com.example.mq.service.bean.dubbo.DubboRequestConfig;
import com.example.mq.service.config.ConfigService;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */
@Service
public class ConfigServiceImpl  implements ConfigService {
	private static Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);

	@Override
	public DubboRegisterConfig getRegisterConfig(String requestName) throws Exception {
		return null;
	}

	@Override
	public DubboRequestConfig getRequestConfig(String requestName) throws Exception {
		return null;
	}
}
