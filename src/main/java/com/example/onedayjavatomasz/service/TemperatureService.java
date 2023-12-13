package com.example.onedayjavatomasz.service;

import com.example.onedayjavatomasz.connector.ElevationApiConnector;
import com.example.onedayjavatomasz.connector.GeocodingApiConnector;
import com.example.onedayjavatomasz.domain.Offset;
import com.example.onedayjavatomasz.domain.Temperature;
import com.example.onedayjavatomasz.dto.AddressRequest;
import com.example.onedayjavatomasz.dto.TemperatureResponse;
import com.example.onedayjavatomasz.repository.OffsetsRepository;
import com.example.onedayjavatomasz.repository.TemperatureRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemperatureService implements TemperatureApiService {

    private final GeocodingApiConnector geocodingApiConnector;
    private final ElevationApiConnector elevationApiConnector;

    private final TemperatureRepository temperatureRepository;

    private final OffsetsRepository offsetsRepository;


    public TemperatureService(GeocodingApiConnector geocodingApiConnector, ElevationApiConnector elevationApiConnector,
                              TemperatureRepository temperatureRepository, OffsetsRepository offsetsRepository) {
        this.geocodingApiConnector = geocodingApiConnector;
        this.elevationApiConnector = elevationApiConnector;
        this.temperatureRepository = temperatureRepository;
        this.offsetsRepository = offsetsRepository;
    }

    @Override
    public TemperatureResponse getTemperatureResponse(AddressRequest addressRequest) {
        var address = getAddressFromAddressRequest(addressRequest);
        var defaultTemperature = getDefaultTemperatureForGivenAddress(address);
        var elevation = getElevationForGivenAddress(address);
        var offsetList = offsetsRepository.findAllOffsetsForGivenElevation(elevation);
        return calculateProperTemperature(defaultTemperature, elevation, offsetList);
    }

    private TemperatureResponse calculateProperTemperature(Double defaultTemperature, Double elevation, List<Offset> offsetList) {
        if (offsetList.isEmpty()) {
            return new TemperatureResponse(elevation, defaultTemperature);
        }
        if (offsetList.size() == 1) {
            Integer offsetValue = offsetList.stream().map(Offset::getAltitudeOffset)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Offset for given min and max not found"));
            var finalTemperature = defaultTemperature + offsetValue;
            return new TemperatureResponse(elevation, finalTemperature);
        } else {
            throw new IllegalArgumentException("It is not possible to calculate proper temperature, " +
                    "because there is more offsets defined for the same altitude");
        }
    }

    private String getAddressFromAddressRequest(AddressRequest addressRequest) {
        return Optional.ofNullable(addressRequest.oneLineAddress())
                .or(() -> parseMultilineAddress(addressRequest))
                .orElseThrow(() -> new IllegalArgumentException("One line address and multiline address is missing"));
    }

    private Optional<? extends String> parseMultilineAddress(AddressRequest addressRequest) {
        return Optional.of(String.join(", ", addressRequest.streetAddress(),
                addressRequest.postalCode() + " " +  addressRequest.city(),
                addressRequest.country()));
    }

    private Double getDefaultTemperatureForGivenAddress(String address) {
        var postcode = parsePostCodeFromOneLineAddress(address);

        return temperatureRepository.findByPostalCode(postcode)
                .map(Temperature::getTemperature)
                .orElseThrow(() -> new RuntimeException("Default temperature for given code not found"));
    }

    private Double getElevationForGivenAddress(String address) {
        var locationMap = geocodingApiConnector.getLocation(address);
        var location = String.format("%s,%s", locationMap.get("lat"), locationMap.get("lng"));
        return elevationApiConnector.getElevation(location);
    }

    private String parsePostCodeFromOneLineAddress(String address) {
        Pattern pattern = Pattern.compile("\\b\\d{2}\\b");
        Matcher matcher = pattern.matcher(address);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalArgumentException("Post code not found in the address:" + address);
    }
}
