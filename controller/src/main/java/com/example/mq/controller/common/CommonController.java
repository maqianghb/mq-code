package com.example.mq.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.base.common.MyException;
import com.example.mq.base.common.Response;
import com.example.mq.base.enums.CityEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/13
 *
 */
@RequestMapping("/common")
@RestController
public class CommonController {
	private static final Logger LOG = LoggerFactory.getLogger(CommonController.class);

	@RequestMapping(value = "/logLevel", method = {RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public Response setLogLevel(@RequestBody String paramStr) throws Exception{
		LOG.info("设置logLevel，paramStr:{}", paramStr);
		JSONObject jsonObj = JSONObject.parseObject(paramStr);
		if(Objects.isNull(jsonObj)){
			throw new MyException("参数转换异常！");
		}
		String logName = StringUtils.isEmpty(jsonObj.getString("name")) ? "root" : jsonObj.getString("name");
		String logLevel = StringUtils.isEmpty(jsonObj.getString("level")) ? "info" : jsonObj.getString("level");
		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		if(!Objects.isNull(loggerContext)){
			ch.qos.logback.classic.Logger chLogger =loggerContext.getLogger(logName);
			if(!Objects.isNull(chLogger)){
				chLogger.setLevel(Level.toLevel(logLevel));
				LOG.info("logger reset level info, logger:{}, level:{}", logName, logLevel);
			}
		}
		return Response.createBySuccess();
	}

	@RequestMapping(value = "/queryCityList", method = {RequestMethod.GET}, produces = "application/json;charset=UTF-8")
	public Response queryCityList() throws Exception{
		List<Map<String, Object>> result =new ArrayList<>();
		for(CityEnum cityEnum : CityEnum.values()){
			Map<String, Object> tmpMap =new HashMap<>(4);
			tmpMap.put("code", cityEnum.getCode());
			tmpMap.put("name", cityEnum.getName());
			result.add(tmpMap);
		}
		return Response.createBySuccess(result);
	}
}
