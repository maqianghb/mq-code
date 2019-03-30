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
import redis.clients.util.Pool;

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
		Object result =null;
		Pool<Jedis> jedisPool = null;
		Jedis jedis =null;
		try {
			if(null ==(jedisPool =codisClient.getJedisPool()) || null ==(jedis =jedisPool.getResource()) ){
				LOG.error("jedis 为空！");
				return result;
			}
			result =jedis.get(key);
		} catch (Exception e) {
			LOG.error("jedis执行出错，key:{}", JSONObject.toJSONString(key), e);
		} finally {
			codisClient.returnResource(jedisPool, jedis);
		}
		return result;
	}

	@Override
	public Map<String, Object> mget(String... keys) {
		if(keys.length ==0){
			throw new IllegalArgumentException("mget 参数为空！");
		}
		Map<String, Object> results =new LinkedHashMap<>(keys.length *2);
		Pool<Jedis> jedisPool = null;
		Jedis jedis =null;
		try {
			if(null ==(jedisPool =codisClient.getJedisPool()) || null ==(jedis =jedisPool.getResource()) ){
				LOG.error("jedis 为空！");
				return results;
			}
			List<String> tmpResult =jedis.mget(keys);
			if (!CollectionUtils.isEmpty(tmpResult)){
				for (int i=0; i<tmpResult.size(); i++){
					results.put(keys[i], tmpResult.get(i));
				}
			}
		} catch (Exception e) {
			LOG.error("jedis执行出错，keys:{}", JSONObject.toJSONString(keys), e);
		} finally {
			codisClient.returnResource(jedisPool, jedis);
		}
		return results;
	}

	@Override
	public Object hget(String hkey, String field) {
		if(StringUtils.isEmpty(hkey) || StringUtils.isEmpty(field)){
			throw new IllegalArgumentException("hget 参数为空！");
		}
		Object result =null;
		Pool<Jedis> jedisPool = null;
		Jedis jedis =null;
		try {
			if(null ==(jedisPool =codisClient.getJedisPool()) || null ==(jedis =jedisPool.getResource()) ){
				LOG.error("jedis 为空！");
				return result;
			}
			result =jedis.hget(hkey, field);
		} catch (Exception e) {
			LOG.error("jedis执行出错，hkey:{}:field:{}", hkey, field, e);
		} finally {
			codisClient.returnResource(jedisPool, jedis);
		}
		return result;
	}

	@Override
	public List<Object> batchHGet(Map<String, String> map) {
		if(null ==map || map.size() ==0){
			throw new IllegalArgumentException("batchHGet 参数为空！");
		}
		List<Object> results = new ArrayList<>(map.size());
		Pool<Jedis> jedisPool = null;
		Jedis jedis =null;
		Pipeline pipeline =null;
		try {
			if(null ==(jedisPool =codisClient.getJedisPool()) || null ==(jedis =jedisPool.getResource())
					|| null ==(pipeline =jedis.pipelined())){
				LOG.error("jedis or pipeline 为空！");
				return results;
			}
			for (Map.Entry<String, String> entry : map.entrySet()){
				pipeline.hget(entry.getKey(), entry.getValue());
			}
			results =pipeline.syncAndReturnAll();
		} catch (Exception e) {
			LOG.error("jedis or pipeline 执行出错，map:{}", JSONObject.toJSONString(map), e);
		} finally {
			codisClient.returnResource(jedisPool, jedis);
		}
		return results;
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
		List<Object> results =new ArrayList<>(hkeys.length);
		Pool<Jedis> jedisPool = null;
		Jedis jedis =null;
		Pipeline pipeline =null;
		try {
			if(null ==(jedisPool =codisClient.getJedisPool()) || null ==(jedis =jedisPool.getResource())
					|| null ==(pipeline =jedis.pipelined())){
				LOG.error("jedis or pipeline 为空！");
				return results;
			}
			for (int i=0; i<hkeys.length; i++){
				pipeline.hgetAll(hkeys[i]);
			}
			results =pipeline.syncAndReturnAll();
		} catch (Exception e) {
			LOG.error("jedis or pipeline 执行出错，hkeys:{}", JSONObject.toJSONString(hkeys), e);
		} finally {
			codisClient.returnResource(jedisPool, jedis);
		}
		return results;
	}

	@Override
	public long setValue(String key, String value, int expireSeconds) {
		if(StringUtils.isEmpty(key) || StringUtils.isEmpty(value)){
			throw new IllegalArgumentException("setValue 参数为空！");
		}
		Pool<Jedis> jedisPool = null;
		Jedis jedis =null;
		try {
			if(null ==(jedisPool =codisClient.getJedisPool()) || null ==(jedis =jedisPool.getResource()) ){
				LOG.error("jedis 为空！");
				return -1;
			}
			jedis.set(key, value);
			if(expireSeconds >0){
				jedis.expire(key, expireSeconds);
			}
		} catch (Exception e) {
			LOG.error("jedis执行出错，key:{}|value:{}", key, value, e);
			return -1;
		} finally {
			codisClient.returnResource(jedisPool, jedis);
		}
		return 1;
	}

	@Override
	public long hsetValue(String key, String field, String value, int expireSeconds) {
		if(StringUtils.isEmpty(key) ||StringUtils.isEmpty(field)|| StringUtils.isEmpty(value)){
			throw new IllegalArgumentException("hsetValue 参数为空！");
		}
		Pool<Jedis> jedisPool = null;
		Jedis jedis =null;
		try {
			if(null ==(jedisPool =codisClient.getJedisPool()) || null ==(jedis =jedisPool.getResource()) ){
				LOG.error("jedis 为空！");
				return -1;
			}
			jedis.hset(key, field, value);
			if(expireSeconds >0){
				jedis.expire(key, expireSeconds);
			}
		} catch (Exception e) {
			LOG.error("hsetValue err, key:{}|field:{}|value:{}|expireSeconds:{}", key, field, value, expireSeconds);
			return -1;
		} finally {
			codisClient.returnResource(jedisPool, jedis);
		}
		return 1;
	}
}
