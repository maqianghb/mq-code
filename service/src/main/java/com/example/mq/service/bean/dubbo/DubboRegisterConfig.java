package com.example.mq.service.bean.dubbo;

import com.alibaba.dubbo.config.RegistryConfig;
import lombok.Data;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */
@Data
public class DubboRegisterConfig {

	private String registerId;

	private String protocol;

	private String address;

	private Boolean check =true;

	public static RegistryConfig convertToRegisterConfig(DubboRegisterConfig config){
		if(null ==config){
			throw new IllegalArgumentException(" parseToRegisterConfig 操作，参数为空！");
		}
		RegistryConfig registryConfig =new RegistryConfig();
		registryConfig.setId(config.getRegisterId());
		registryConfig.setProtocol(config.getProtocol());
		registryConfig.setAddress(config.getAddress());
		if(null !=config.getCheck()){
			registryConfig.setCheck(config.getCheck());
		}
		return registryConfig;
	}
}
