package com.example.mq.service.config;

import com.example.mq.service.bean.dubbo.DubboRegisterConfig;
import com.example.mq.service.bean.dubbo.DubboRequestConfig;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */

public interface ConfigService {

	/**
	 * 获取dubbo特征的注册中心配置
	 * @return
	 * @throws Exception
	 */
	DubboRegisterConfig getRegisterConfig(String requestName) throws Exception;

	/**
	 * 获取dubbo特征的dubbo请求配置
	 * @return
	 * @throws Exception
	 */
	DubboRequestConfig getRequestConfig(String requestName) throws Exception;
}
