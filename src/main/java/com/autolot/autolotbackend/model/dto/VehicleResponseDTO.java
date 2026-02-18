package com.autolot.autolotbackend.model.dto;

import com.autolot.autolotbackend.model.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VehicleResponseDTO(
        String id,
        String make,
        String model,
        Integer year,
        BigDecimal price,
        Integer mileage,
        String vin,
        VehicleCondition condition,
        Transmission transmission,
        FuelType fuelType,
        BodyType bodyType,
        String exteriorColor,
        String interiorColor,
        String description,
        Boolean featured,
        VehicleStatus vehicleStatus,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
