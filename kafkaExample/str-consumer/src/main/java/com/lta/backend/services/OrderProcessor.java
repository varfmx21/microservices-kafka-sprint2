package com.lta.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderProcessor {

    public String process(String topic, int partition, String message){

        String normalizedMessage = message == null ? "" : message.toLowerCase();
        String response;

        if ("unsc-topic".equals(topic)) {
            if (partition == 0 || normalizedMessage.contains("chief")) {
                response = "\"I need a weapon.\" - Master Chief";
            } else if (partition == 1 || normalizedMessage.contains("cortana")) {
                response = "\"Don't make a girl a promise... if you know you can't keep it.\" - Cortana";
            } else {
                response = "\"Wake me... when you need me.\" - Master Chief";
            }
        } else if ("covenant-topic".equals(topic)) {
            response = "\"The Great Journey waits for no one, brother.\" - Prophet of Truth";
        } else if ("flood-topic".equals(topic)) {
            response = "\"I am a monument to all your sins.\" - Gravemind";
        } else if ("forerunner-response-topic".equals(topic)) {
            response = "\"The Mantle of Responsibility shelters all.\" - Forerunner Lore";
        } else {
            response = "\"Spartans never die, they are only missing in action.\" - UNSC Protocol";
        }

        log.info("Generated response for topic {} partition {}: {}", topic, partition, response);

        return response;
    }
}