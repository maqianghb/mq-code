package com.example.mq.base.mysql;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Properties;

public class MysqlConnConfig {

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

    public MysqlConnConfig(){}

    public MysqlConnConfig(Properties properties) {
        this.host = properties.getProperty("mysql.host");
        this.port = Integer.parseInt(properties.getProperty("mysql.port"));
        this.user = properties.getProperty("mysql.user");
        this.password = properties.getProperty("mysql.password");
        this.db = properties.getProperty("mysql.db");

    }


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getDb() {
		return db;
	}
}
