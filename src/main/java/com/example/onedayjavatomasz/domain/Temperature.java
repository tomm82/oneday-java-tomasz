package com.example.onedayjavatomasz.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "temperatures")
public class Temperature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postalCode;
    private Double temperature;

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Temperature that = (Temperature) o;
        return id.equals(that.id) && postalCode.equals(that.postalCode) && temperature.equals(that.temperature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postalCode, temperature);
    }
}
