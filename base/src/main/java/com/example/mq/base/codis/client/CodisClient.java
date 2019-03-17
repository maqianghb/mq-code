package com.example.mq.base.codis.client;

import com.example.mq.base.codis.jodis.CodisResourcePool;
import com.example.mq.base.codis.jodis.RoundRobinCodisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/3/11
 *
 */
@Component
public class CodisClient {
	private static Logger LOG = LoggerFactory.getLogger(CodisClient.class);

	@Autowired
	private CodisConfig codisConfig;

	private volatile CodisResourcePool codisResourcePool;

	private CodisResourcePool getCodisResourcePool() {
		//慢加载，第一次使用时加载
		if (codisResourcePool == null) {
			synchronized (this) {
				if (codisResourcePool == null) {
					codisResourcePool = RoundRobinCodisPool.create()
							.curatorClient(codisConfig.getCodisConnZkService(), 30000)
							.zkProxyDir("/jodis/" + codisConfig.getCodisConnName())
							.password(codisConfig.getCodisConnPassword())
							.poolConfig(new JedisPoolConfig())
							.build();
				}
			}
		}
		return codisResourcePool;
	}

	public Pool<Jedis> getJedisPool() {
		return this.getCodisResourcePool().getPool();
	}

	public Jedis getJedis(){
		Pool<Jedis> jedisPool = null;
		Jedis jedis = null;
		try {
			if(null ==(jedisPool = this.getJedisPool()) || null ==(jedis =jedisPool.getResource())){
				LOG.warn("get jedis failed!");
			}
		} catch (Exception e) {
			LOG.error("get jedis err, exception:", e);
		}
		return jedis;
	}
}
