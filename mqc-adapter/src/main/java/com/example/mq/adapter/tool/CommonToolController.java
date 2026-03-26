package com.example.mq.adapter.tool;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mq.client.tool.CommonToolClient;
import com.example.mq.common.base.MqcResponse;
import com.example.mq.common.enums.CityEnum;
import com.example.mq.common.enums.base.BizErrorEnum;
import com.example.mq.common.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/11/13
 *
 */
@RestController
@RequestMapping(value = "/api/mqc/code/common/tool")
@Slf4j
public class CommonToolController implements CommonToolClient {

	@Override
	public Object queryCityList(String operator) {
		AssertUtils.assertNotBlank(operator, BizErrorEnum.PARAM_INVALID);

		List<JSONObject> cityList =Arrays.asList();
		for(CityEnum cityEnum : CityEnum.values()){
			JSONObject cityJson =new JSONObject();
			cityJson.put("code", cityEnum.getCode());
			cityJson.put("name", cityEnum.getName());
			cityList.add(cityJson);
		}

		return MqcResponse.success(cityList);
	}

	@Override
	public Object setLogLevel(Object object) {
		AssertUtils.assertNotNull(object, BizErrorEnum.PARAM_INVALID);

		JSONObject jsonObj = JSON.parseObject(JSON.toJSONString(object));
		String name = jsonObj.getString("name");
		String level = jsonObj.getString("level");
		AssertUtils.assertNotBlank(name, BizErrorEnum.PARAM_INVALID);
		AssertUtils.assertNotBlank(level, BizErrorEnum.PARAM_INVALID);

		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger chLogger =loggerContext.getLogger(name);
		if(chLogger != null){
			chLogger.setLevel(Level.toLevel(level));
			log.info("日志级别调整完成, name:{}, level:{}", name, level);
		}

		return MqcResponse.success();
	}

}
