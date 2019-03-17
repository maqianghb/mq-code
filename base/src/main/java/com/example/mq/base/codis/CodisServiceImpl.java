package com.example.mq.base.codis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.example.mq.base.codis.client.CodisClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/3/11
 *
 */
@Service
public class CodisServiceImpl implements CodisService {
	private static final Logger LOG = LoggerFactory.getLogger(CodisServiceImpl.class);

	@Autowired
	CodisClient codisClient;


	@Override
	public Object get(String key) {
		if(StringUtils.isEmpty(key)){
			throw new IllegalArgumentException("get 参数为空！");
		}
		Jedis jedis =codisClient.getJedis();
		if(null ==jedis){
			LOG.error("get 获取jedis为空！");
			return null;
		}
		return jedis.get(key);
	}

	@Override
	public Map<String, Object> mget(String... keys) {
		if(keys.length ==0){
			throw new IllegalArgumentException("mget 参数为空！");
		}
		Jedis jedis =codisClient.getJedis();
		if(null ==jedis){
			LOG.error("get 获取jedis为空！");
			return null;
		}
		List<String> values =jedis.mget(keys);
		Map<String, Object> results =new LinkedHashMap<>();
		if(!CollectionUtils.isEmpty(values)){
			for(int i=0; i<keys.length; i++){
				results.put(keys[i], values.get(i));
			}
		}
		return results;
	}

	@Override
	public Object hget(String hkey, String field) {
		if(StringUtils.isEmpty(hkey) || StringUtils.isEmpty(field)){
			throw new IllegalArgumentException("hget 参数为空！");
		}
		Jedis jedis =codisClient.getJedis();
		if(null ==jedis){
			LOG.error("get 获取jedis为空！");
			return null;
		}
		return jedis.hget(hkey, field);
	}

	@Override
	public List<Object> batchHGet(Map<String, String> map) {
		if(null ==map || map.size() ==0){
			throw new IllegalArgumentException("batchHGet 参数为空！");
		}
		List<Object> result = new ArrayList<>(map.size());
		Jedis jedis =null;
		Pipeline pipeline =null;
		try {
			if(null ==(jedis =codisClient.getJedis()) || null ==(pipeline =jedis.pipelined())){
				LOG.error("jedis or pipeline 为空！");
				return result;
			}
			for (Map.Entry<String, String> entry : map.entrySet()){
				pipeline.hget(entry.getKey(), entry.getValue());
			}
			result=pipeline.syncAndReturnAll();
		} catch (Exception e) {
			LOG.error("jedis or pipeline 执行出错，map:{}", JSONObject.toJSONString(map), e);
		} finally {
			try {
				if(null !=pipeline){
					pipeline.close();
				}
			} catch (IOException e) {
				LOG.error("pipeline 关闭异常！", e);
			}
		}
		return result;
	}

	@Override
	public Map<String, Object> hmget(String hkey, String... fields) {
		return null;
	}

	@Override
	public Map<String, Object> hgetAll(String hkey) {
		return null;
	}

	@Override
	public List<Object> batchHGetAll(String... hkeys) {
		if(hkeys.length ==0){
			throw new IllegalArgumentException("batchHGetAll 参数为空！");
		}
		//get value
		List<Object> values =new ArrayList<>(hkeys.length);
		Jedis jedis =null;
		Pipeline pipeline =null;
		try {
			if(null ==(jedis =codisClient.getJedis()) || null ==(pipeline =jedis.pipelined())){
				LOG.error("jedis or pipeline 为空！");
				return null;
			}
			for (int i=0; i<hkeys.length; i++){
				pipeline.hgetAll(hkeys[i]);
			}
			values =pipeline.syncAndReturnAll();
		} catch (Exception e) {
			LOG.error("jedis or pipeline 执行出错，hkeys:{}", JSONObject.toJSONString(hkeys), e);
		} finally {
			try {
				if(null !=pipeline){
					pipeline.close();
				}
			} catch (IOException e) {
				LOG.error("pipeline 关闭异常！", e);
			}
		}
		return values;
	}

	@Override
	public long setValue(String key, String value, int expireSeconds) {
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)){
			throw new IllegalArgumentException("setValue 参数为空！");
		}
		Jedis jedis =codisClient.getJedis();
		if(null ==jedis){
			LOG.error("get 获取jedis为空！");
			return -1;
		}
		try {
			jedis.set(key, value);
			if(expireSeconds >0){
				jedis.expire(key, expireSeconds);
			}
		} catch (Exception e) {
			LOG.error("setValue err, key:{}|value:{}|expireSeconds:{}", key, value, expireSeconds);
			return -1;
		}
		return 1;
	}

	@Override
	public long hsetValue(String key, String field, String value, int expireSeconds) {
		if(StringUtils.isEmpty(key) ||StringUtils.isEmpty(field)|| StringUtils.isEmpty(value)){
			throw new IllegalArgumentException("hsetValue 参数为空！");
		}
		Jedis jedis =codisClient.getJedis();
		if(null ==jedis){
			LOG.error("get 获取jedis为空！");
			return -1;
		}
		try {
			jedis.hset(key, field, value);
			if(expireSeconds >0){
				jedis.expire(key, expireSeconds);
			}
		} catch (Exception e) {
			LOG.error("hsetValue err, key:{}|field:{}|value:{}|expireSeconds:{}", key, field, value, expireSeconds);
			return -1;
		}
		return 1;
	}
}
