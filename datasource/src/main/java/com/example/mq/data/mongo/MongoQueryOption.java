package com.example.mq.data.mongo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
}
