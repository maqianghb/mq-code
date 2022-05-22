package com.example.mq.base.mysql;

import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/21
 *
 */
@Component
public class DruidUtil {
	private static final Logger LOG = LoggerFactory.getLogger(DruidUtil.class);

	@Autowired
	private DruidConfig druidConfig;

	public DruidDataSource createDataSource(String url,String userName,String password){
		DruidDataSource druidDataSource = new DruidDataSource();
		try {
			druidDataSource.setUrl(url);
			druidDataSource.setUsername(userName);
			druidDataSource.setPassword(password);
			druidDataSource.setDriverClassName(druidConfig.getDriver_class_name());
			druidDataSource.setInitialSize(druidConfig.getInitial_size());
			druidDataSource.setMaxActive(druidConfig.getMax_active());
			druidDataSource.setMinIdle(druidConfig.getMin_idle());
			druidDataSource.setMaxWait(druidConfig.getMax_wait());
			druidDataSource.setDbType(druidConfig.getDb_type());
			druidDataSource.setPoolPreparedStatements(druidConfig.isPool_prepared_statements());
			druidDataSource.setTestOnBorrow(druidConfig.isTest_on_borrow());
			druidDataSource.setTestOnReturn(druidConfig.isTest_on_return());
			druidDataSource.setTestWhileIdle(druidConfig.isTest_while_idle());
			druidDataSource.setTimeBetweenEvictionRunsMillis(druidConfig.getTime_between_eviction_runs_millis());
			druidDataSource.setValidationQuery(druidConfig.getValidation_query());
			druidDataSource.setFilters(druidConfig.getFilters());
			druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidConfig.getMax_pool_prepared_statement_per_connection_size());
			druidDataSource.setMinEvictableIdleTimeMillis(druidConfig.getMin_evictable_idle_time_millis());
			druidDataSource.setConnectionProperties(druidConfig.getConnection_properties());
		} catch (SQLException e) {
			LOG.error("create druid datasource err!", e);
		}
		return druidDataSource;
	}
}
