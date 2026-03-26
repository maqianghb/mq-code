package com.example.mq.app.util;

import java.util.HashMap;
import java.util.Map;

import com.example.mq.app.utils.JexlExecuteUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/12/6
 *
 */
public class JexlExecuteUtilsTest {

	@Test
	public void checkExps() {
		String exps ="((a+b)/c+d)*e+f";
		boolean checkResult = JexlExecuteUtils.checkExps(exps);
		Assert.assertTrue( checkResult);
	}

	@Test
	public void evaluateExps() {
		String exps ="";
		Map<String, Object> paramsMap =new HashMap<>(8);
		paramsMap.put("", 10000);
		paramsMap.put("", 4000);
		paramsMap.put("", 0.05);
		paramsMap.put("", 0.1);
		Object value =JexlExecuteUtils.evaluateExps(exps, paramsMap);
	}
}