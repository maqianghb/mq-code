package com.example.mq.data.mongo;

import com.example.mq.data.util.SnowflakeIdGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MongoConstant {

	private static final SnowflakeIdGenerator idGenerator =new SnowflakeIdGenerator(10L,11L);

    @Value("${mongodb.table.customer}")
    public String TABLE_CUSTOMER;

    @Value("${mongodb.table.seller}")
    public String TABLE_SELLER;

    public static Long createId(){
    	return idGenerator.nextId();
	}

	public static String createStrId(){
    	return String.valueOf(idGenerator.nextId());
	}

}
