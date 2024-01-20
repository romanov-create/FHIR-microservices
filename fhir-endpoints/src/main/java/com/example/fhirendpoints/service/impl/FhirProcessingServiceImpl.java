package com.example.fhirendpoints.service.impl;

import com.example.fhirendpoints.service.FhirProcessingService;
import com.example.fhirendpoints.service.KafkaProducerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FhirProcessingServiceImpl implements FhirProcessingService {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public void processAndSendEncounters(String bundleJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode bundleNode = objectMapper.readTree(bundleJson);

            JsonNode entryNode = bundleNode.path("entry");
            if (entryNode.isArray()) {
                for (JsonNode entry : entryNode) {
                    JsonNode resourceNode = entry.path("resource");
                    if (resourceNode.has("resourceType") && resourceNode.get("resourceType").asText().equals("Encounter")) {
                        if (resourceNode.has("status") && resourceNode.get("status").asText().equals("finished")) {
                            removeExternalReferences(resourceNode);

                            String encounterJson = objectMapper.writeValueAsString(resourceNode);

                            kafkaProducerService.sendEncountersToKafka(encounterJson);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeExternalReferences(JsonNode resourceNode) {
        if (resourceNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) resourceNode;

            removeReference(objectNode, "subject");
            removeReference(objectNode, "individual");
            removeReference(objectNode, "location");
            removeReference(objectNode, "serviceProvider");

            objectNode.fields().forEachRemaining(entry -> {
                JsonNode childNode = entry.getValue();
                removeExternalReferences(childNode);
            });
        } else if (resourceNode.isArray()) {
            for (JsonNode arrayElement : resourceNode) {
                removeExternalReferences(arrayElement);
            }
        }
    }

    private void removeReference(ObjectNode objectNode, String propertyName) {
        if (objectNode.has(propertyName)) {
            JsonNode propertyNode = objectNode.get(propertyName);
            if (propertyNode.isObject() && propertyNode.has("reference")) {
                ((ObjectNode) propertyNode).remove("reference");
            }
        }
    }
}
