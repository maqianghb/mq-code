package com.example.mq.service.datasource;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.mq.data.mysql.DruidUtil;
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
 * @create: 2019/1/22
 *
 */
@Configuration
@MapperScan(basePackages = {"com.example.mq.core.dao.seller"}, sqlSessionTemplateRef = "sellerSqlSessionTemplate")
public class DataSourceSeller {

	@Value("${jdbc.seller.mapper_xml}")
	private String mapperXml;

	@Value("${jdbc.seller.url}")
	private String url;
	@Value("${jdbc.seller.username}")
	private String userName;
	@Value("${jdbc.seller.password}")
	private String password;


	@Autowired
	private DruidUtil druidUtil;

	@Bean(name = "sellerDataSource")
	public DruidDataSource dataSource() {
		return druidUtil.createDataSource(url,userName,password);
	}

	@Bean(name = "sellerSqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("sellerDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperXml));
		return bean.getObject();
	}

	@Bean(name = "sellerTransactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("sellerDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "sellerSqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sellerSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
