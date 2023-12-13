package com.example.onedayjavatomasz.service;

import com.example.onedayjavatomasz.dto.AddressRequest;
import com.example.onedayjavatomasz.dto.TemperatureResponse;

public interface TemperatureApiService {

    TemperatureResponse getTemperatureResponse(AddressRequest addressRequest);
}
