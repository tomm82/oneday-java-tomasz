package com.example.onedayjavatomasz.connector;

public interface ElevationApiConnector {
    /**
     * Get elevation for given location
     * @param location location in format: latitude,longitude
     * @return elevation
     */
    Double getElevation(String location);
}
