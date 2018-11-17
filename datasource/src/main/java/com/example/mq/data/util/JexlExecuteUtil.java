package com.example.mq.data.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/10/29
 *
 */

public class JexlExecuteUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JexlExecuteUtil.class);

	private static JexlEngine jexl = new Engine();

	public static Boolean checkExps(String exps){
		if(StringUtils.isEmpty(exps)){
			return false;
		}
		try {
			jexl.createExpression(exps);
		} catch (Exception e) {
			LOG.error("表达式创建失败，exps:", exps, e);
			return false;
		}
		return true;
	}

	public static Object evaluateExps(String exps, Map<String, Object> paramsMap){
		if(StringUtils.isEmpty(exps)){
			return null;
		}
		JexlContext ctxt =new MapContext();
		if( !CollectionUtils.isEmpty(paramsMap)){
			for(Map.Entry<String, Object> entry :paramsMap.entrySet()){
				ctxt.set(entry.getKey(), entry.getValue());
			}
		}
		Object value =jexl.createExpression(exps).evaluate(ctxt);
		return value;
	}

	public static void main(String[] args){
		String exps ="((a*1.0+ b*1.5)*2.0 +c)*2.5+d";
		System.out.println("---check result:"+checkExps(exps).toString());

		Map<String, Object> params =new HashMap<>();
		params.put("a", 1.0);
		params.put("b", 1.5);
		params.put("c", 2.0);
		params.put("d", 2.5);
		System.out.println("---evaluate result:"+evaluateExps(exps, params).toString());
	}
}
