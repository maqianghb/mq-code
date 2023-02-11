package com.example.mq.data.mysql;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: mq-code
 * @description: 从mySql库加载配置项
 * @author: maqiang
 * @create: 2018-10-13 12:17
 */
public class MySqlConfigLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MySqlConfigLoader.class);

    private static final String CONN_URL_FORMAT ="jdbc:mysql://%s:%s/%s?characterEncoding=utf-8&useSSL=false";
    private static final String GLOBAL_CONFIG_SQL ="select group, key, value from global_config where del_flag !=1 ";
    private static final String COMMON_CONFIG_SQL ="select group, key, value from common_config where del_flag !=1 ";

    private static ConcurrentHashMap<String, Map<String, String>> globalConfig =new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Map<String, String>> commonConfig =new ConcurrentHashMap<>();

    // 连接mysql，加载mySql中的配置信息
    static {
        //读mySql.properties中配置数据
        Properties properties = new Properties();
        try {
            InputStream in = MySqlConfigLoader.class.getClassLoader().getResourceAsStream("mySql.properties");
            properties.load(in);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }

        //加载mySql驱动
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("加载mysql驱动失败", e);
        }
        // 连接mysql数据库
        MysqlConnConfig mysqlConnConfig = new MysqlConnConfig(properties);
        Connection mysqlConn = null;
        String url = String.format(CONN_URL_FORMAT, mysqlConnConfig.getHost(), String.valueOf(mysqlConnConfig.getPort()),
                mysqlConnConfig.getDb());
        try {
            mysqlConn = DriverManager.getConnection(url, mysqlConnConfig.getUser(), mysqlConnConfig.getPassword());
        } catch (SQLException e) {
            LOG.error("连接mysql数据库失败，url:{}", url, e);
        }
        LOG.info("连接mysql数据库成功，url:" + url);

        // mySQL config
        try {
            queryGlobalConfig(mysqlConn);
            queryCommonConfig(mysqlConn);
        } catch (Exception e) {
            LOG.error("query mySql config err!", e);
        }finally {
            if( !Objects.isNull(mysqlConn)){
                try {
                    mysqlConn.close();
                } catch (SQLException e) {
                    LOG.error("close mySql conn err!", e);
                }
            }
        }
    }

    private static void queryGlobalConfig(Connection conn) throws Exception{
        LOG.info("start query global config!");
        PreparedStatement ps =null;
        ps =conn.prepareStatement(GLOBAL_CONFIG_SQL);
        ResultSet resultSet =ps.executeQuery();
        while(resultSet.next()){
            String group = resultSet.getString(1);
            String key = resultSet.getString(2);
            String value = resultSet.getString(3);
            putConfig(globalConfig, group, key, value);
        }
        return;
    }

    private static void queryCommonConfig(Connection conn) throws Exception{
        LOG.info("start query common config!");
        PreparedStatement ps =null;
        ps =conn.prepareStatement(COMMON_CONFIG_SQL);
        ResultSet resultSet =ps.executeQuery();
        while(resultSet.next()){
            String group = resultSet.getString(1);
            String key = resultSet.getString(2);
            String value = resultSet.getString(3);
            putConfig(commonConfig, group, key, value);
        }
        return;
    }

    private static void putConfig(ConcurrentHashMap<String, Map<String,String>> configMap,
                             String group, String key, String value){
        if(StringUtils.isEmpty(group) ||StringUtils.isEmpty(key) ||StringUtils.isEmpty(value)){
            LOG.warn("参数不合规，group:{}|key:{}|value:{}", group, key, value);
            return;
        }
        Map<String, String> keyMap =new HashMap<>();
        if(configMap.containsKey(group)){
            keyMap =configMap.get(group);
        }
        keyMap.putIfAbsent(key, value);
        configMap.put(group, keyMap);
        return;
    }

    public static void main(String[] args) throws Exception{
        String group ="testGroup";
        String key1 ="testKey1";
        String value1 ="testValue1";
        String key2 ="testKey2";
        String value2 ="testValue2";
        String value3 ="testValue3";
        putConfig(globalConfig, group, key1,value1);
        putConfig(globalConfig, group, key2,value2);
        putConfig(globalConfig, group, key2,value3);

        putConfig(commonConfig, group, key1,value1);
        putConfig(commonConfig, group, key2,value2);
        putConfig(commonConfig, group, key2,value3);

    }
}