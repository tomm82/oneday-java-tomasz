package com.example.onedayjavatomasz.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class GoogleMapsElevationApiConnector implements ElevationApiConnector {
    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.elevation.api.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public GoogleMapsElevationApiConnector(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Double getElevation(String location) {
        String uriString = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("locations", location)
                .queryParam("key", apiKey).toUriString();
        var response = restTemplate.getForEntity(uriString, Map.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return getElevationFromResponseBody(response);
        }
        throw new RuntimeException("Missing response");
    }

    private Double getElevationFromResponseBody(ResponseEntity<Map> response) {
        Map<String, Object> responseBody = response.getBody();
        if (!((List)responseBody.get("results")).isEmpty()) {
            Map<String, Object> result = (Map<String, Object>) ((List)responseBody.get("results")).get(0);
            return Double.valueOf(String.valueOf(result.get("elevation")));
        }
        throw new RuntimeException("Missing elevation for given address");
    }
}
