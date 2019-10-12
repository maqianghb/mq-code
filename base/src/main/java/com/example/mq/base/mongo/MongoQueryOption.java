package com.example.mq.base.mongo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: mq-code
 * @description: mongo查询条件生成
 * @author: maqiang
 * @create: 2018/11/15
 *
 */

public class MongoQueryOption {
	private static final Logger LOG = LoggerFactory.getLogger(MongoQueryOption.class);

	/**
	 * 左匹配
	 * @param key
	 * @param content
	 * @return
	 */
	public static Map<String, Object> leftMatchOption(String key, String content){
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(content)){
			return new HashMap<>();
		}
		Pattern pattern = Pattern.compile("^" + content+ ".*$", Pattern.CASE_INSENSITIVE);
		Map<String, Object> options =new HashMap<>(1);
		options.put(key, pattern);
		return options;
	}

	/**
	 * 右匹配
	 * @param key
	 * @param content
	 * @return
	 */
	public static Map<String, Object> rightMatchOption(String key, String content){
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(content)){
			return new HashMap<>();
		}
		Pattern pattern = Pattern.compile("^.*" + content+ "$", Pattern.CASE_INSENSITIVE);
		Map<String, Object> options =new HashMap<>(1);
		options.put(key, pattern);
		return options;
	}

	/**
	 * 模糊匹配
	 * @param key
	 * @param content
	 * @return
	 */
	public static Map<String, Object> fuzzyMatchOption(String key, String content){
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(content)){
			return new HashMap<>();
		}
		Pattern pattern = Pattern.compile("^.*" + content+ ".*$", Pattern.CASE_INSENSITIVE);
		Map<String, Object> options =new HashMap<>(1);
		options.put(key, pattern);
		return options;
	}

	/**
	 * key是否存在
	 * @param key
	 * @param exist
	 * @return
	 */
	public static Map<String, Object> existOptions(String key, boolean exist){
		if(StringUtils.isEmpty(key)){
			return new HashMap<>();
		}
		Map<String, Object> options =new HashMap<>(1);
		options.put(key, new BasicDBObject("$exists", exist));
		return options;
	}

	public static <T> Map<String, Object> inOptions(String key, List<T> list){
		if(StringUtils.isEmpty(key) || CollectionUtils.isEmpty(list)){
			return new HashMap<>();
		}
		Map<String, Object> options =new HashMap<>();
		options.put(key, new BasicDBObject("$in", list));
		return options;
	}

	public static void main(String[] args) {
		String key1 ="testKey1";
		Map<String, Object> options =MongoQueryOption.existOptions(key1, true);
		String key2 ="testKey2";
		List<Integer> list = Arrays.asList(10010, 10011, 10012);
		options.putAll(MongoQueryOption.inOptions(key2, list));
		System.out.println("options:" + JSONObject.toJSONString(options));
	}
}
