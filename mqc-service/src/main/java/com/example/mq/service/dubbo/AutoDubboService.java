package com.example.mq.service.dubbo;

import java.util.List;

import com.example.mq.service.bean.dubbo.DubboRegisterConfig;
import com.example.mq.service.bean.dubbo.DubboRequestConfig;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */
public interface AutoDubboService {

	/**
	 * dubbo泛化执行调用
	 * @param dubboRegisterConfig
	 * @param dubboRequestConfig
	 * @param paramList
	 * @return
	 * @throws Exception
	 */
	Object generalizedExecute(DubboRegisterConfig dubboRegisterConfig, DubboRequestConfig dubboRequestConfig,
			List<Object> paramList) throws Exception;
}
