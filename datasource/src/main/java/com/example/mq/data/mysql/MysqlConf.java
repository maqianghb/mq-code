package com.example.mq.data.mysql;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Properties;

@Data
public class MysqlConf {

    @NotNull
    private String host;

    @NotNull
    private int port;

    @NotNull
    private String user;

    @NotNull
    private String password;

    @NotNull
    private String db;

    public MysqlConf(){}

    public MysqlConf(Properties properties) {
        this.host = properties.getProperty("mysql.host");
        this.port = Integer.parseInt(properties.getProperty("mysql.port"));
        this.user = properties.getProperty("mysql.user");
        this.password = properties.getProperty("mysql.password");
        this.db = properties.getProperty("mysql.db");

    }

}
