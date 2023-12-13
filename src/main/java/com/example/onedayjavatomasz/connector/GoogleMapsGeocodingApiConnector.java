package com.example.onedayjavatomasz.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class GoogleMapsGeocodingApiConnector implements GeocodingApiConnector {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.geocoding.api.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public GoogleMapsGeocodingApiConnector(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getLocation(String address) {
        String uriString = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("address", address)
                .queryParam("key", apiKey).toUriString();
        System.out.println(uriString);
        var response = restTemplate.getForEntity(uriString, Map.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return getLocationFromResponseBody(response);
        }
        throw new RuntimeException("Missing response");
    }

    private static Map<String, Object> getLocationFromResponseBody(ResponseEntity<Map> response) {
        Map<String, Object> responseBody = response.getBody();
        if (!((List)responseBody.get("results")).isEmpty()) {
            Map<String, Object> firstResult = (Map<String, Object>) ((List)responseBody.get("results")).get(0);
            Map<String, Object> geometry = (Map<String, Object>) firstResult.get("geometry");
            return  (Map<String, Object>) geometry.get("location");
        }
        return Map.of();
    }

}
