package com.example.onedayjavatomasz.connector;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class GoogleMapsGeocodingApiConnector implements GeocodingApiConnector {

    public static final String QUERY_PARAMETER_ADDRESS = "address";
    public static final String QUERY_PARAMETER_API_KEY = "key";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.geocoding.api.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public GoogleMapsGeocodingApiConnector(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> getLocation(String address) {
        String uriString = prepareUri(address);
        System.out.println(uriString);
        var response = restTemplate.getForEntity(uriString, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return getLocationFromResponseBody(response.getBody());
        }
        throw new RuntimeException("Missing response");
    }

    private String prepareUri(String address) {
        String uriString = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam(QUERY_PARAMETER_ADDRESS, address)
                .queryParam(QUERY_PARAMETER_API_KEY, apiKey).toUriString();
        return uriString;
    }

    private static Map<String, Double> getLocationFromResponseBody(String body) {
        Double latitude = JsonPath.parse(body).read("$.results[0].geometry.location.lat");
        Double longitude = JsonPath.parse(body).read("$.results[0].geometry.location.lng");
        return Map.of(LATITUDE, latitude, LONGITUDE, longitude);
    }

}
