package com.example.mq.base.codis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import com.example.mq.controller.ControllerApplication;
import com.example.mq.base.util.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/3/11
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ControllerApplication.class)
public class CodisServiceTest {

	private CodisService codisService;

	@Before
	public void setUp() throws Exception {
		codisService = SpringContextUtil.getBean(CodisService.class);
	}

	@Test
	public void get() {
		String testKey ="mq:test_key_1";

		Object result =codisService.get(testKey);
		Assert.assertTrue(null != result);
	}

	@Test
	public void mget() {
		List<String> keys =new ArrayList<>();
		keys.add("mq:test_key_1");
		keys.add("mq:test_key_2");
		keys.add("mq:test_key_3");

		Map<String, Object> results =codisService.mget(keys.toArray(new String[keys.size()]));
		Assert.assertTrue(null != results && results.size() >0);
	}

	@Test
	public void hget() {
	}

	@Test
	public void batchHGet() {
		Map<String, String> keyMaps =new LinkedHashMap<>();
		keyMaps.put("mq:test_hkey_1", "test_field_2");
		keyMaps.put("mq:test_hkey_2", "test_field_1");

		List<Object> results =codisService.batchHGet(keyMaps);
		Assert.assertTrue(!CollectionUtils.isEmpty(results));
	}

	@Test
	public void hmget() {
	}

	@Test
	public void hgetAll() {
	}

	@Test
	public void batchHGetAll() {
		List<String> keys =new ArrayList<>();
		keys.add("mq:test_hkey_1");
		keys.add("mq:test_hkey_2");

		List<Object> results =codisService.batchHGetAll(keys.toArray(new String[keys.size()]));
		Assert.assertTrue(!CollectionUtils.isEmpty(results));
	}

	@Test
	public void setValue() {
		codisService.setValue("mq:test_key_1", "test_value_1", 60 * 60);
		codisService.setValue("mq:test_key_2", "test_value_2", 60 * 60);
		codisService.setValue("mq:test_key_3", "test_value_3", 60 * 60);
		Assert.assertTrue(true);
	}

	@Test
	public void hsetValue() {
		codisService.hsetValue("mq:test_hkey_1", "test_field_1", "test_value_1_1", 60 * 60);
		codisService.hsetValue("mq:test_hkey_1", "test_field_2", "test_value_1_2", 60 * 60);

		codisService.hsetValue("mq:test_hkey_2", "test_field_1", "test_value_2_1", 60 * 60);
		codisService.hsetValue("mq:test_hkey_2", "test_field_2", "test_value_2_2", 60 * 60);
		codisService.hsetValue("mq:test_hkey_2", "test_field_3", "test_value_2_3", 60 * 60);
		Assert.assertTrue(true);
	}
}