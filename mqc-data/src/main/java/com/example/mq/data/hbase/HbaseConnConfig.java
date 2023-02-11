package com.example.mq.data.hbase;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/6/3
 *
 */
//@Component
public class HbaseConnConfig {
	private static Logger LOG = LoggerFactory.getLogger(HbaseConnConfig.class);

	@Value("${hbase.zookeeper.property.clientPort}")
	private String hbaseZkClientPort;

	@Value("${hbase.zookeeper.quorum}")
	private String hbaseZkQuorum;

	@Value("${hbase.zookeeper.node.parent}")
	private String hbaseZkNodeParent;


	@Bean("hbaseConnection")
	public Connection configuration(){
		Configuration conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", hbaseZkClientPort);
		conf.set("hbase.zookeeper.quorum", hbaseZkQuorum);
		conf.set("zookeeper.znode.parent", hbaseZkNodeParent);
		LOG.info("hbase connection config, clientPort:{}|quorum:{}|parent:{}", hbaseZkClientPort,
				hbaseZkQuorum, hbaseZkNodeParent);

		Connection conn =null;
		try {
			//connection线程安全，整个进程共享一个即可
			conn  = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			LOG.error(" hbaseConnection create err, conf:{}", JSONObject.toJSONString(conf), e);
		}
		return conn;
	}

}
