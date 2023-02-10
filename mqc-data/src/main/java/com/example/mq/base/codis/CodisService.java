package com.example.mq.base.codis;

import java.util.List;
import java.util.Map;


/**
 * @program: mq-code
 * @description: Codis操作接口
 * @author: maqiang
 * @create: 2019/3/11
 *
 */
public interface CodisService {

	/**
	 * 根据key获取value值，若key的value不存在，则为null值。
	 * @param key
	 * @return
	 */
	Object get(final String key);

	/**
	 * 获取多个key的值，若某个key的value不存在，则为null值。
	 * @param keys
	 * @return map(key, value)
	 */
	Map<String, Object> mget(final String... keys);

	/**
	 * 获取key对应的hash数据field域的值
	 * @param hkey
	 * @param field
	 * @return
	 */
	Object hget(final String hkey, final String field);

	/**
	 *批量执行hget操作
	 * @param map(hkey, field)
	 * @return
	 */
	List<Object> batchHGet(Map<String, String> map);

	/**
	 * 获取key对应的hash数据多个field的值
	 * @param hkey
	 * @param fields
	 * @return	map(field, value)
	 */
	Map<String, Object> hmget(final String hkey, final String... fields);

	/**
	 *获取key对应的完整的hash数据
	 * @param hkey
	 * @return map(field, value)
	 */
	Map<String, Object> hgetAll(final String hkey);

	/**
	 *批量执行hgetAll
	 * @param hkeys
	 * @return
	 */
	List<Object> batchHGetAll(final String... hkeys);


	/**
	 * expireSeconds<=0表示永久有效
	 * @param key
	 * @param value
	 * @param expireSeconds
	 * @return
	 */
	long setValue(final String key,final String value, int expireSeconds);

	/**
	 * expireSeconds<=0表示永久有效
	 * @param key
	 * @param filed
	 * @param value
	 * @param expireSeconds
	 * @return
	 */
	long hsetValue(final String key, final String filed, final String value, int expireSeconds);
}
