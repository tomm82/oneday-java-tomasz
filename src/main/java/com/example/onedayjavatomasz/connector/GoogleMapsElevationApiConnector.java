package com.example.onedayjavatomasz.connector;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class GoogleMapsElevationApiConnector implements ElevationApiConnector {
    public static final String LOCATIONS = "locations";
    public static final String API_KEY = "key";
    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.elevation.api.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public GoogleMapsElevationApiConnector(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Double getElevation(String location) {
        String uriString = prepareUri(location);
        var response = restTemplate.getForEntity(uriString, Map.class);
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return JsonPath.parse(response.getBody()).read("$.results[0].elevation");
        }
        throw new RuntimeException("Missing response body containing information about elevation");
    }

    private String prepareUri(String location) {
        String uriString = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam(LOCATIONS, location)
                .queryParam(API_KEY, apiKey).toUriString();
        return uriString;
    }

}
