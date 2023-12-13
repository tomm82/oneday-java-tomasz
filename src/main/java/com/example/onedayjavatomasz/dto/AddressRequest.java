package com.example.onedayjavatomasz.dto;

public record AddressRequest(
        String oneLineAddress,
        String streetAddress,
        String postalCode,
        String city,
        String country

) {
}
