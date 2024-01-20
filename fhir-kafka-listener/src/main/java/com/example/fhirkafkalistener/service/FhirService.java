package com.example.fhirkafkalistener.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FhirService {
    public static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FhirService.class);

    private final HttpClient httpClient;
    private final String fhirServerUrl;

    public FhirService(HttpClient httpClient, @Value("${fhir.server.url}") String fhirServerUrl) {
        this.httpClient = httpClient;
        this.fhirServerUrl = fhirServerUrl;
    }

    public void createEncounter(String encounterJson) throws Exception {
        String endpoint = fhirServerUrl + "/Encounter";

        HttpPost httpPost = new HttpPost(endpoint);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(encounterJson));

        HttpResponse response = httpClient.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity());


        if (statusCode == 201) {
            logger.info("Encounter entry was successfully sent to the fhir server.");
        } else {
            logger.error("Error sending Encounter. Response code: " + statusCode);
            logger.error("Response body: " + responseBody);
        }
    }
}
