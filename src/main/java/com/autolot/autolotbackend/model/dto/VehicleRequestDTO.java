package com.autolot.autolotbackend.model.dto;

import com.autolot.autolotbackend.model.entity.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record VehicleRequestDTO(
        @NotBlank(message = "make is required")
        String make,

        @NotBlank(message = "model is required")
        String model,

        @NotNull(message = "year is required")
        @Min(value = 1900, message = "Year must be 1900 or later")
        @Max(value = 2026, message = "Year cannot be in the future")
        Integer year,

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "mileage is required")
        @PositiveOrZero(message = "Mileage cannot be negative")
        @Max(value = 1000000, message = "Mileage exceeds reasonable maximum")
        Integer mileage,

        String vin,

        @NotNull(message = "condition is required")
        VehicleCondition condition,

        @NotNull(message = "transmission is required")
        Transmission transmission,

        @NotNull(message = "fuel type is required")
        FuelType fuelType,

        @NotNull(message = "body type is required")
        BodyType bodyType,

        @NotBlank(message = "exterior color is required")
        String exteriorColor,

        String interiorColor,
        String description,
        Boolean featured
) {}
