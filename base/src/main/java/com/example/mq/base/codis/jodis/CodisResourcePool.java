package com.example.mq.base.codis.jodis;

import java.io.Closeable;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;


/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/3/11
 *
 */

public interface CodisResourcePool extends Closeable {

	Jedis getResource();

	Pool<Jedis> getPool();

	void returnResource(Pool<Jedis> pool, Jedis jedis);

}
