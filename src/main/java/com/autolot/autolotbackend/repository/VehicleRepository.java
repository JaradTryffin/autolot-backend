package com.autolot.autolotbackend.repository;

import com.autolot.autolotbackend.model.entity.Vehicle;
import com.autolot.autolotbackend.model.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    Optional<Vehicle> findByVin(String vin);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByFeaturedTrue();

    List<Vehicle> findByMakeIgnoreCase(String make);

    List<Vehicle> findByMakeIgnoreCaseAndModelIgnoreCase(String make, String model);

    List<Vehicle> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<Vehicle> findByMileageLessThanEqual(int mileage);

    List<Vehicle> findByPriceLessThanEqual(BigDecimal price);

    List<Vehicle> findByPriceGreaterThanEqual(BigDecimal price);

    List<Vehicle> findByStatusOrderByPriceAsc(VehicleStatus status);

    List<Vehicle> findByStatusOrderByCreatedAtDesc(VehicleStatus status);

}

