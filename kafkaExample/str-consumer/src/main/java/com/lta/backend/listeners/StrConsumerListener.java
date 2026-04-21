package com.lta.backend.listeners;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.lta.backend.services.OrderProcessor;

@Log4j2
@Component
public class StrConsumerListener {
    
    @Autowired
    private OrderProcessor orderProcessor;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // Listener para mensajes UNSC (particiones 0 y 1)
    // Consume del tópico "unsc-topic" con partición 0 y 1
    @KafkaListener(groupId = "group-unsc",
            topicPartitions = {
                @TopicPartition(topic = "unsc-topic", partitions = {"0", "1"})
            },
            containerFactory = "validMessageContainerFactory")
    public void listenerUnsc(String message) {
        log.info("LISTENER UNSC ::: Recibiendo un mensaje {}", message);
        String menu = orderProcessor.process(message);
        log.info("Suggested menu: {}", menu);
        // Envía respuesta al tópico de respuestas
        kafkaTemplate.send("unsc-topic-response", menu);
    }

    // Listener para mensajes COVENANT (partición 0)
    // Consume del tópico "covenant-topic" con partición 0
    @KafkaListener(groupId = "group-covenant",
            topicPartitions = @TopicPartition(topic = "covenant-topic", partitions = {"0"}),
            containerFactory = "validMessageContainerFactory")
    public void listenerCovenant(String message) {
        log.info("LISTENER COVENANT ::: Recibiendo un mensaje {}", message);
        String menu = orderProcessor.process(message);
        log.info("Suggested menu: {}", menu);
        // Envía respuesta al tópico de respuestas
        kafkaTemplate.send("covenant-topic-response", menu);
    }

    // Listener para mensajes FLOOD (partición 0)
    // Consume del tópico "flood-topic" con partición 0
    @KafkaListener(groupId = "group-flood",
            topicPartitions = @TopicPartition(topic = "flood-topic", partitions = {"0"}),
            containerFactory = "validMessageContainerFactory")
    public void listenerFlood(String message) {
        log.info("LISTENER FLOOD ::: Recibiendo un mensaje {}", message);
        String menu = orderProcessor.process(message);
        log.info("Suggested menu: {}", menu);
        // Envía respuesta al tópico de respuestas
        kafkaTemplate.send("flood-topic-response", menu);
    }

    // Listener para mensajes FORERUNNER
    // Consume del tópico "forerunner-response-topic"
    @KafkaListener(groupId = "group-forerunner",
            topics = "forerunner-response-topic",
            containerFactory = "validMessageContainerFactory")
    public void listenerForerunner(String message) {
        log.info("LISTENER FORERUNNER ::: Recibiendo un mensaje {}", message);
        String menu = orderProcessor.process(message);
        log.info("Suggested menu: {}", menu);
        // Envía respuesta al tópico de respuestas
        kafkaTemplate.send("forerunner-response", menu);
    }

}






