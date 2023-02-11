package com.example.mq.core.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.rpc.service.GenericException;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/4/29
 *
 */

public class DubboUtils {
	private static Logger LOG = LoggerFactory.getLogger(DubboUtils.class);

	/**
	 * 泛化调用dubbo接口
	 * @param referenceConfig
	 * @param method
	 * @param paramList
	 * @return
	 * @throws Exception
	 */
	public static Object invoke(ReferenceConfig referenceConfig, String method, List<Object> paramList){
		if(null ==referenceConfig || StringUtils.isEmpty(method)){
			throw new IllegalArgumentException(" invoke 操作，参数为空！");
		}
		//genericService
		GenericService genericService = (GenericService) referenceConfig.get();
		if(null ==genericService){
			LOG.error(" genericService is empty, referenceConfig:{}", JSONObject.toJSONString(referenceConfig));
			return null;
		}

		//paramTypes
		String[] paramTypes =null;
		List<String> paramTypeList =getMethodParamTypes(referenceConfig.getInterface(), method);
		if(!CollectionUtils.isEmpty(paramTypeList)){
			paramTypes = paramTypeList.toArray(new String[]{});
		}
		Object[] params =null;
		if(!CollectionUtils.isEmpty(paramList)){
			params =paramList.toArray();
		}

		//invoke
		Object result = null;
		try {
			result = genericService.$invoke(method, paramTypes, params);
		} catch (GenericException e) {
			LOG.error(" invoke err, interface:{}||method:{}|paramList:{}", referenceConfig.getInterface(),
					method, JSONObject.toJSONString(paramList));
		}
		return result;
	}

	/**
	 * 获取dubbo接口参数类型列表
	 * @param interfaceName
	 * @param methodName
	 * @return
	 */
	public static List<String> getMethodParamTypes(String interfaceName, String methodName){
		if(StringUtils.isEmpty(interfaceName) || StringUtils.isEmpty(methodName)){
			throw new IllegalArgumentException(" getMethodParamTypes 操作，参数为空！");
		}
		Class<?> clazz = null;
		try {
			clazz = Class.forName(interfaceName);
		} catch (ClassNotFoundException e) {
			LOG.error(" find class err, interfaceName:{}", interfaceName, e);
			return null;
		}
		Method[] methods =clazz.getMethods();
		for(Method method: methods){
			if(method.getName().equals(methodName)){
				Class[] paramTypes =method.getParameterTypes();
				if(null ==paramTypes || paramTypes.length ==0){
					return null;
				}
				List<String> result = new ArrayList<>(paramTypes.length);
				for(Class paramClass : paramTypes){
					result.add(paramClass.getTypeName());
				}
				return result;
			}
		}
		return null;
	}
}
