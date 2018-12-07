package com.example.mq.data.zk;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description: Zk配置数据
 * @author Qiang.Ma7
 */
@Component
@Data
public class ZkConfig {

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

}
