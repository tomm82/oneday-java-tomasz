package com.example.onedayjavatomasz.domain;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "offsets")
public class Offset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double fromMeters;
    private Double toMeters;
    private Integer altitudeOffset;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getFromMeters() {
        return fromMeters;
    }

    public void setFromMeters(Double fromMeters) {
        this.fromMeters = fromMeters;
    }

    public Double getToMeters() {
        return toMeters;
    }

    public void setToMeters(Double toMeters) {
        this.toMeters = toMeters;
    }

    public Integer getAltitudeOffset() {
        return altitudeOffset;
    }

    public void setAltitudeOffset(Integer altitudeOffset) {
        this.altitudeOffset = altitudeOffset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offset offset1 = (Offset) o;
        return id.equals(offset1.id) && fromMeters.equals(offset1.fromMeters) && toMeters.equals(offset1.toMeters) && altitudeOffset.equals(offset1.altitudeOffset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromMeters, toMeters, altitudeOffset);
    }
}
