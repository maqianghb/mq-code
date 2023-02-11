package com.example.mq.data.mysql;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/21
 *
 */
@Component
@ConfigurationProperties(prefix = "druid.datasource")
@Data
public class DruidConfig {

	private String db_type;
	private String driver_class_name;
	private int initial_size;
	private int max_active;
	private int min_idle;
	private long max_wait;
	private boolean pool_prepared_statements;
	private boolean test_on_borrow;
	private boolean test_on_return;
	private boolean test_while_idle;
	private long time_between_eviction_runs_millis;
	private String validation_query;
	private String filters;
	private int max_pool_prepared_statement_per_connection_size;
	private long min_evictable_idle_time_millis;
	private String connection_properties;

}
