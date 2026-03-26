package com.example.mq.app.dubbo;

import com.example.mq.app.dubbo.model.DubboRegisterConfig;
import com.example.mq.app.dubbo.model.DubboRequestConfig;

import java.util.List;

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
