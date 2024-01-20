package com.example.fhirendpoints.controller;

import com.example.fhirendpoints.service.FhirProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fhir")
public class FhirController {

    @Autowired
    private FhirProcessingService fhirProcessingService;

    @PreAuthorize("hasAuthority('ROLE_API')")
    @PostMapping("/bundle")
    public ResponseEntity<String> processFhirBundle(@RequestBody String bundleJson) {
        try {
            fhirProcessingService.processAndSendEncounters(bundleJson);
            return ResponseEntity.ok("FHIR Bundle is processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing FHIR Bundle:" + e.getMessage());
        }
    }
}
