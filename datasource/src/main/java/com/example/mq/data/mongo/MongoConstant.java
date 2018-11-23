package com.example.mq.data.mongo;

import com.example.mq.data.util.SnowflakeIdWorker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MongoConstant {

	private static final SnowflakeIdWorker idWorker =new SnowflakeIdWorker(10L,11L);

    @Value("${mongodb.table.customer}")
    public String TABLE_CUSTOMER;

    @Value("${mongodb.table.seller}")
    public String TABLE_SELLER;

    public static final String GREATER_THAN_KEY ="$gt";
	public static final String GREATER_THAN_OR_EQUAL_KEY ="$gte";


    public static Long createId(){
    	return idWorker.nextId();
	}

	public static String createStrId(){
    	return String.valueOf(idWorker.nextId());
	}

}
