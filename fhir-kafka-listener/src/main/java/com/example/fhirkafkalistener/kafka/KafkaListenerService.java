package com.example.fhirkafkalistener.kafka;

import com.example.fhirkafkalistener.service.FhirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {

    @Autowired
    private FhirService fhirService;

    @KafkaListener(topics = "fhir-encounters")
    public void listen(String message) {
        try {
            fhirService.createEncounter(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
