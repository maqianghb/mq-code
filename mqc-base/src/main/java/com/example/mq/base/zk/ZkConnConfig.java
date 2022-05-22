package com.example.mq.base.zk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description: Zk配置数据
 * @author Qiang.Ma7
 */
@Component
public class ZkConnConfig {

	@Value("${zookeeper.server.address}")
    private String zkAddress;

	@Value("${zookeeper.server.sessionTimeoutMs}")
    private Integer sessionTimeoutMs;

	@Value("${zookeeper.server.connectionTimeoutMs}")
    private Integer connectionTimeoutMs;

	@Value("${zookeeper.server.sleepTime}")
    private Integer sleepTime;

	@Value("${zookeeper.server.maxRetries}")
    private Integer maxRetries;

	@Value("${zookeeper.server.namespace}")
    private String namespace;


	public String getZkAddress() {
		return zkAddress;
	}

	public Integer getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public Integer getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}

	public Integer getSleepTime() {
		return sleepTime;
	}

	public Integer getMaxRetries() {
		return maxRetries;
	}

	public String getNamespace() {
		return namespace;
	}
}
