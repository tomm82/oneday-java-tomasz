package com.example.onedayjavatomasz.service;

import com.example.onedayjavatomasz.connector.ElevationApiConnector;
import com.example.onedayjavatomasz.connector.GeocodingApiConnector;
import com.example.onedayjavatomasz.domain.Address;
import com.example.onedayjavatomasz.domain.Offset;
import com.example.onedayjavatomasz.domain.Temperature;
import com.example.onedayjavatomasz.dto.AddressRequest;
import com.example.onedayjavatomasz.dto.TemperatureResponse;
import com.example.onedayjavatomasz.mapper.InboundMapper;
import com.example.onedayjavatomasz.repository.OffsetsRepository;
import com.example.onedayjavatomasz.repository.TemperatureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemperatureService implements TemperatureApiService {

    private final GeocodingApiConnector geocodingApiConnector;
    private final ElevationApiConnector elevationApiConnector;

    private final TemperatureRepository temperatureRepository;

    private final OffsetsRepository offsetsRepository;

    private final InboundMapper inboundMapper;


    public TemperatureService(GeocodingApiConnector geocodingApiConnector, ElevationApiConnector elevationApiConnector,
                              TemperatureRepository temperatureRepository, OffsetsRepository offsetsRepository, InboundMapper inboundMapper) {
        this.geocodingApiConnector = geocodingApiConnector;
        this.elevationApiConnector = elevationApiConnector;
        this.temperatureRepository = temperatureRepository;
        this.offsetsRepository = offsetsRepository;
        this.inboundMapper = inboundMapper;
    }

    @Override
    public TemperatureResponse getTemperatureResponse(AddressRequest addressRequest) {
        var address = inboundMapper.getAddressFromAddressRequest(addressRequest);
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



    private Double getDefaultTemperatureForGivenAddress(Address address) {
        String postCode = address.getPostalCode();
        return temperatureRepository.findByPostalCode(postCode)
                .map(Temperature::getTemperature)
                .orElseThrow(() -> new IllegalArgumentException("Default temperature for given postal code not found"));
    }

    private Double getElevationForGivenAddress(Address address) {
        String addressAsOneLine = String.format("%s, %s %s, %s", address.getStreetAddress(), address.getPostalCode(),
                address.getCity(), address.getCountry());
        var locationMap = geocodingApiConnector.getLocation(addressAsOneLine);
        var location = String.format("%s,%s", locationMap.get("latitude"), locationMap.get("longitude"));
        return elevationApiConnector.getElevation(location);
    }

}
