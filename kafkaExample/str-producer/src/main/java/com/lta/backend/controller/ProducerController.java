package com.lta.backend.controller;

import com.lta.backend.services.ResponseListener;
import com.lta.backend.services.StringProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private StringProducerService producer;

    @PostMapping
    public ResponseEntity<String> sendFromFrontend(@RequestBody String message) {
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El mensaje no puede estar vacio");
        }

        producer.sendMessage(message.trim());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Mensaje enviado a Kafka");
    }

    @GetMapping
    public ResponseEntity<String> sendMenu(@RequestParam(required = false) String message) throws InterruptedException {
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.ok(
                    ResponseListener.response != null
                            ? ResponseListener.response
                            : "Esperando respuesta..."
            );
        }

        return ResponseEntity.ok(sendAndWait(message.trim()));
    }

    private String sendAndWait(String message) throws InterruptedException {

        ResponseListener.response = null;

        producer.sendMessage(message);

        int attempts = 0;

        while(ResponseListener.response == null && attempts < 10){
            Thread.sleep(500);
            attempts++;
        }

        return ResponseListener.response != null
                ? ResponseListener.response
                : "No response (posiblemente fue a partición 0)";
    }
}