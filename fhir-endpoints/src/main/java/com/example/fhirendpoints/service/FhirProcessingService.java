package com.example.fhirendpoints.service;

public interface FhirProcessingService {

    void processAndSendEncounters(String bundleJson);

}
