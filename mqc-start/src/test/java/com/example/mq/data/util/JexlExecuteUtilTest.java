package com.example.mq.data.util;

import java.util.HashMap;
import java.util.Map;

import com.example.mq.common.utils.JexlExecuteUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2018/12/6
 *
 */
public class JexlExecuteUtilTest {

	@Test
	public void checkExps() {
		String exps ="((a+b)/c+d)*e+f";
		boolean checkResult = JexlExecuteUtil.checkExps(exps);
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
		Object value =JexlExecuteUtil.evaluateExps(exps, paramsMap);
	}
}