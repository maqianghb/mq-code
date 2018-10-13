package com.example.mq.data.mongo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhengbo
 * @date 2018/7/24
 */
@Component
public class MongoConstant {

    @Value("${mongodb.table.customer}")
    public String TABLE_CUSTOMER;

    @Value("${mongodb.table.seller}")
    public String TABLE_SELLER;

}
