package com.lta.backend.services;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResponseListener {

    public static String response = null;

    // Listen to all response topics from different consumer services
    @KafkaListener(topics = {"unsc-topic-response", "covenant-topic-response", "flood-topic-response", "forerunner-response"}, groupId = "group-producer-responses")
    public void listen(String message){
        response = message;
    }
}