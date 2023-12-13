package com.example.onedayjavatomasz.controller;

import com.example.onedayjavatomasz.dto.AddressRequest;
import com.example.onedayjavatomasz.dto.TemperatureResponse;
import com.example.onedayjavatomasz.service.TemperatureApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TemperatureController {

    private final TemperatureApiService temperatureApiService;

    public TemperatureController(TemperatureApiService temperatureApiService) {
        this.temperatureApiService = temperatureApiService;
    }

    @PostMapping("/temperatures")
    public ResponseEntity<TemperatureResponse> getTemperature(@RequestBody AddressRequest addressRequest) {
        TemperatureResponse temperatureResponse = temperatureApiService.getTemperatureResponse(addressRequest);
        return ResponseEntity.ok(temperatureResponse);
    }
}
