package com.example.mq.data.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
public class MongodbConfig {

    @Value("${mongodb.userName}")
    private  String user;
    @Value("${mongodb.password}")
    private  String pass;
    @Value("${mongodb.dataBaseName}")
    private  String database;
    @Value("${mongodb.host}")
    private  String serverAddr;
    @Value("${mongodb.port}")
    private  int port;

    @Bean("mongoDatabase")
    public MongoDatabase mongoDatabase(){
        List<ServerAddress> list = new ArrayList<>();
        MongoCredential credential = MongoCredential.createCredential(user, database,pass.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(3000)
                .threadsAllowedToBlockForConnectionMultiplier(10)
                .readPreference(ReadPreference.secondaryPreferred())
                .build();
        String[] serverAddrs = serverAddr.split(",");
        for (String ip : serverAddrs) {
            list.add(new ServerAddress(ip, port));
        }
        MongoClient mongoClient = new MongoClient(list, Arrays.asList(credential), options);
        return mongoClient.getDatabase(database);
    }
}
