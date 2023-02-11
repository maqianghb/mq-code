package com.example.mq.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MongoConstant {

    @Value("${mongodb.table.customer}")
    public String TABLE_CUSTOMER;

    @Value("${mongodb.table.seller}")
    public String TABLE_SELLER;


    public static final String GREATER_THAN_KEY ="$gt";
	public static final String GREATER_THAN_OR_EQUAL_KEY ="$gte";
	public static final String LESSER_THAN_KEY ="$lt";
	public static final String LESSER_THAN_OR_EQUAL_KEY ="$lte";
	public static final String NOT_EQUAL_KEY ="$ne";
	public static final String IN_LIST__KEY ="$in";
	public static final String NOT_IN_LIST__KEY ="$nin";

}
