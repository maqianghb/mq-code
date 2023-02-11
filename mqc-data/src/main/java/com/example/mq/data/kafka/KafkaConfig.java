package com.example.mq.data.kafka;

import lombok.Data;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Data
public class KafkaConfig {


//    @Value("${risk.kafka.broker.list}")
    public String riskKafkaBrokerList;

    private String groupId;

    private String topic;


//    @Bean("riskKafkaProducer")
    public KafkaProducer createProducer(){
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", riskKafkaBrokerList);
        kafkaProperties.put("acks", "all");
        kafkaProperties.put("auto.create.topics.enable",true);
        kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, Object> producer = new KafkaProducer<>(kafkaProperties);
        return producer;
    }


}
