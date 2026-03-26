package com.example.mq.app.dubbo.impl;

import com.example.mq.app.dubbo.DubboConfigService;
import com.example.mq.app.dubbo.model.DubboRegisterConfig;
import com.example.mq.app.dubbo.model.DubboRequestConfig;
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
public class DubboConfigServiceImpl implements DubboConfigService {
	private static Logger LOG = LoggerFactory.getLogger(DubboConfigServiceImpl.class);

	@Override
	public DubboRegisterConfig getRegisterConfig(String requestName) throws Exception {
		return null;
	}

	@Override
	public DubboRequestConfig getRequestConfig(String requestName) throws Exception {
		return null;
	}
}
