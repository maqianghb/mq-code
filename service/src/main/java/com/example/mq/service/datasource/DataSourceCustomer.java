package com.example.mq.service.datasource;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.mq.base.mysql.DruidUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * @program: mq-code
 * @description: ${description}
 * @author: maqiang
 * @create: 2019/1/21
 *
 */
@Configuration
@MapperScan(basePackages = {"com.example.mq.service.dao.customer"}, sqlSessionTemplateRef = "customerSqlSessionTemplate")
public class DataSourceCustomer {

	@Value("${jdbc.customer.mapper_xml}")
	private String mapperXml;

	@Value("${jdbc.customer.url}")
	private String url;
	@Value("${jdbc.customer.username}")
	private String userName;
	@Value("${jdbc.customer.password}")
	private String password;


	@Autowired
	private DruidUtil druidUtil;

	@Bean(name = "customerDataSource")
	public DruidDataSource dataSource() {
		return druidUtil.createDataSource(url,userName,password);
	}

	@Bean(name = "customerSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("customerDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperXml));
		return bean.getObject();
	}

	@Bean(name = "customerTransactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("customerDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "customerSqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("customerSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

}
