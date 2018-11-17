package com.example.mq.service.zk;

import lombok.Data;

/**
 * @description: Zk配置数据
 * @author Qiang.Ma7
 */
@Data
public class ZkConfig {

    private String zkAddress;
    private Integer sessionTimeout;
    private Integer connectionTimeout;
    private Integer sleepTime;
    private Integer maxRetries;

}
