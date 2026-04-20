package com.lta.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StringProducerService {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String message){

        int partition;
        String topic; // 1. unsc-topic, 2.- covenant-topic, 3.- flood-topic, 4.- forerunner-response-topic.

        if(message.contains("unsc")){
            topic = "unsc-topic";
            if (message.contains("chief")){
                partition = 0;
            } else if (message.contains("cortana")){
                partition = 1;
            }
        } else if(message.contains("covenant")){
            topic = "covenant-topic";
            if (message.contains("prophet")) {
                partition = 0;
            }
        } else if(message.contains("flood")) {
            topic = "flood-topic";
            if (message.contains("gravemind")) {
                partition = 0;
            }
        } else if(message.contains("forerunner")){
            topic = "forerunner-response-topic";
        } else {
            topic = "none";
        }


        kafkaTemplate.send(topic, partition,null, message).whenComplete((result,ex) -> {
            if(ex != null){
                log.error("Error, al enviar el mensaje: {}",ex.getMessage());
            }
            log.info("Mensaje enviado con exito: {}",result.getProducerRecord().value());
            log.info("Particion {}, Offset {}", result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
        });
    }
}