package com.example.onedayjavatomasz.repository;

import com.example.onedayjavatomasz.domain.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {

    Optional<Temperature> findByPostalCode(String postalCode);
}
