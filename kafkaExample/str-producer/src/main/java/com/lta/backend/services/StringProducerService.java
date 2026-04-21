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
        if (message == null || message.trim().isEmpty()) {
            log.warn("Mensaje vacio: no se enviara a Kafka");
            return;
        }

        Integer partition = 0;
        String topic = null; // 1. unsc-topic, 2.- covenant-topic, 3.- flood-topic, 4.- forerunner-response-topic.
        String normalizedMessage = message.toLowerCase();

        if(normalizedMessage.contains("unsc")){
            topic = "unsc-topic";
            if (normalizedMessage.contains("chief")){
                partition = 0;
            } else if (normalizedMessage.contains("cortana")){
                partition = 1;
            }
        } else if(normalizedMessage.contains("covenant")){
            topic = "covenant-topic";
        } else if(normalizedMessage.contains("flood")) {
            topic = "flood-topic";
        } else if(normalizedMessage.contains("forerunner")){
            topic = "forerunner-response-topic";
        }

        if (topic == null) {
            log.warn("Mensaje sin regla de enrutamiento, no se enviara a Kafka: {}", message);
            return;
        }


        kafkaTemplate.send(topic, partition,null, message).whenComplete((result,ex) -> {
            if(ex != null){
                log.error("Error, al enviar el mensaje: {}",ex.getMessage());
                return;
            }
            log.info("Mensaje enviado con exito: {}",result.getProducerRecord().value());
            log.info("Topic: {}, Particion: {}, Offset: {}", result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        });
    }
}