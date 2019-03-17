package com.example.mq.base.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/10/29
 *
 */

public class JexlExecuteUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JexlExecuteUtil.class);

	/**
	 * 表达式计算引擎
	 */
	private static JexlEngine jexl = new Engine();

	private static final ReentrantLock lock =new ReentrantLock();

	public static Boolean checkExps(String exps){
		if(StringUtils.isEmpty(exps)){
			throw new IllegalArgumentException("参数为空！");
		}
		boolean result=false;
		try {
			lock.tryLock();
			jexl.createExpression(exps);
			result =true;
		} catch (Exception e) {
			LOG.error("表达式创建失败，exps:", exps, e);
		}finally {
			jexl.clearCache();
			lock.unlock();
		}
		return result;
	}

	public static Object evaluateExps(String exps, Map<String, Object> params){
		if(StringUtils.isEmpty(exps) || null ==params){
			throw new IllegalArgumentException("参数为空！");
		}
		JexlContext ctxt =new MapContext();
		for(Map.Entry<String, Object> entry :params.entrySet()){
			ctxt.set(entry.getKey(), entry.getValue());
		}
		Object value =null;
		try {
			lock.tryLock();
			value =jexl.createExpression(exps).evaluate(ctxt);
		} catch (Exception e) {
			LOG.error("表达式执行失败，exps:{}|params:{}", exps, JSONObject.toJSONString(params), e);
		}finally {
			jexl.clearCache();
			lock.unlock();
		}
		return value;
	}

	public static void main(String[] args){
		String exps =" ((a*1.0+ b*1.5)  *2.0 +c)*2.5+d ";
		Map<String, Object> params =new HashMap<>();
		params.put("a", 1.0);
		params.put("b", 1.5);
		params.put("c", 2.0);
		params.put("d", 2.5);

		System.out.println("------check result:"+checkExps(exps).toString());
		System.out.println("------evaluate result:"+evaluateExps(exps, params).toString());
	}
}
