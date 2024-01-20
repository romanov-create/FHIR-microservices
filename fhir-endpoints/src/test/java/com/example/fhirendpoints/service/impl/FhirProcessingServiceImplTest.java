package com.example.fhirendpoints.service.impl;

import com.example.fhirendpoints.service.KafkaProducerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FhirProcessingServiceImplTest {


    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private FhirProcessingServiceImpl fhirProcessingService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testProcessAndSendEncounters_PositiveScenario() throws Exception {
        fhirProcessingService.processAndSendEncounters(loadJsonFromFile(true));

        verify(kafkaProducerService, times(1)).sendEncountersToKafka(anyString());
    }

    @Test
    void testProcessAndSendEncounters_NegativeScenario_InvalidBundle() {
        String invalidBundleJson = "{ \"not_entry\": []}";

        fhirProcessingService.processAndSendEncounters(invalidBundleJson);

        verify(kafkaProducerService, never()).sendEncountersToKafka(anyString());
    }

    @Test
    void testProcessAndSendEncounters_NegativeScenario_UnfinishedEncounter() throws Exception {
        fhirProcessingService.processAndSendEncounters(loadJsonFromFile(false));

        verify(kafkaProducerService, never()).sendEncountersToKafka(anyString());
    }

    @Test
    void testProcessAndSendEncounters_CheckRemovedReferences() throws Exception {
        fhirProcessingService.processAndSendEncounters(loadJsonFromFile(true));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaProducerService).sendEncountersToKafka(argumentCaptor.capture());

        String capturedArgument = argumentCaptor.getValue();
        JsonNode processedEncounterNode = objectMapper.readTree(capturedArgument);

        assertTrue(isReferenceRemoved(processedEncounterNode, "subject"));
        assertTrue(isReferenceRemoved(processedEncounterNode, "individual"));
        assertTrue(isReferenceRemoved(processedEncounterNode, "location"));
        assertTrue(isReferenceRemoved(processedEncounterNode, "serviceProvider"));
    }

    private String loadJsonFromFile(boolean isFinished) throws Exception {
        String path = "src/test/resources/";
        return Files.readString(Path.of(path + (isFinished ? "validEncounterWithExternalReferences" : "notValidEncountersNotFinished") + ".json"));
    }

    public static boolean isReferenceRemoved(JsonNode resourceNode, String propertyName) {
        if (resourceNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) resourceNode;

            if (objectNode.has(propertyName)) {
                JsonNode propertyNode = objectNode.get(propertyName);
                return !propertyNode.has("reference");
            }
        }

        return true;
    }

}