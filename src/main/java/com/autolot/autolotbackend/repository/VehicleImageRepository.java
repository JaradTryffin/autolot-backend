package com.autolot.autolotbackend.repository;

import com.autolot.autolotbackend.model.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, String> {
    List<VehicleImage> findByVehicle_IdOrderByDisplayOrderAsc(String vehicleId);
}
