package com.example.onedayjavatomasz.connector;

import java.util.Map;

public interface GeocodingApiConnector {

    /**
     * Get location for given address
     * @param address address as a one line string
     * @return map with latitude as key and longitude as value
     */
    Map<String, Object> getLocation(String address);
}
