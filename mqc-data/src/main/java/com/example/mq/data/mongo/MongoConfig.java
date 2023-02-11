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
public class MongoConfig {

	@Value("${mongodb.host:127.0.0.1}")
	private  String serverAddr;
	@Value("${mongodb.port}")
	private  int port;
	@Value("${mongodb.dataBaseName}")
	private  String database;

    @Value("${mongodb.userName}")
    private  String user;
    @Value("${mongodb.password}")
    private  String pass;



    @Bean("mongoDatabase")
    public MongoDatabase mongoDatabase(){
        List<ServerAddress> list = new ArrayList<>();
        MongoCredential credential = MongoCredential.createCredential(user, database,pass.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(3000)
                .threadsAllowedToBlockForConnectionMultiplier(10)
				//设置读从节点的数据，若有严格时序要求的操作，再考虑改为读主节点数据
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
