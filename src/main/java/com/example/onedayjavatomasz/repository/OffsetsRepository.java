package com.example.onedayjavatomasz.repository;

import com.example.onedayjavatomasz.domain.Offset;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OffsetsRepository extends CrudRepository<Offset, Long> {

    @Query(value = "from Offset t where fromMeters < :elevation and toMeters > :elevation")
    List<Offset> findAllOffsetsForGivenElevation(@Param("elevation") Double elevation);
}
