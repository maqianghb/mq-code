package com.example.mq.app.dubbo;

import com.example.mq.app.dubbo.model.DubboRegisterConfig;
import com.example.mq.app.dubbo.model.DubboRequestConfig;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */

public interface DubboConfigService {

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
