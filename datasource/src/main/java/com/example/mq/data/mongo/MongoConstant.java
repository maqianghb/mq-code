package com.example.mq.data.mongo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MongoConstant {

    @Value("${mongodb.table.customer}")
    public String TABLE_CUSTOMER;

    @Value("${mongodb.table.seller}")
    public String TABLE_SELLER;

}
