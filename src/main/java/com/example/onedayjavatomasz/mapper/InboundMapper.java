package com.example.onedayjavatomasz.mapper;

import com.example.onedayjavatomasz.domain.Address;
import com.example.onedayjavatomasz.dto.AddressRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InboundMapper {

    public Address getAddressFromAddressRequest(AddressRequest addressRequest) {
        return Optional.ofNullable(addressRequest.oneLineAddress())
                .map(this::parseOneLineAddress)
                .orElseGet(() -> parseMultilineAddress(addressRequest));
    }

    private Address parseOneLineAddress(String addr) {
        String[] splitAddress = addr.split(",");
        String streetAddress = Optional.ofNullable(splitAddress[0]).map(String::strip).orElse(null);
        Optional<String[]> postalCodeWithCityOptional = Optional.ofNullable(splitAddress[1])
                .map(String::strip)
                .map(postCodeWithCity -> postCodeWithCity.split(" "));
        String postalCode = null;
        String city = null;
        if (postalCodeWithCityOptional.isPresent()) {
            String[] postalCodeWithCity = postalCodeWithCityOptional.get();
            postalCode = postalCodeWithCity[0];
            city = postalCodeWithCity[1];
        }
        String country = Optional.ofNullable(splitAddress[2]).map(String::strip).orElse(null);
        return Address.builder()
                .streetAddress(streetAddress)
                .postalCode(postalCode)
                .city(city)
                .country(country)
                .build();
    }

    private Address parseMultilineAddress(AddressRequest addressRequest) {
        return Address.builder()
                .streetAddress(addressRequest.streetAddress())
                .postalCode(addressRequest.postalCode())
                .city(addressRequest.city())
                .country(addressRequest.country())
                .build();
    }
}
