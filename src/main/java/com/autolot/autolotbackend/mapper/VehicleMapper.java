package com.autolot.autolotbackend.mapper;

import com.autolot.autolotbackend.model.dto.VehicleRequestDTO;
import com.autolot.autolotbackend.model.dto.VehicleResponseDTO;
import com.autolot.autolotbackend.model.entity.Vehicle;
import com.autolot.autolotbackend.model.entity.VehicleImage;

import java.util.List;

public class VehicleMapper {

    public static Vehicle toEntity(VehicleRequestDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(dto.make());
        vehicle.setModel(dto.model());
        vehicle.setYear(dto.year());
        vehicle.setPrice(dto.price());
        vehicle.setMileage(dto.mileage());
        vehicle.setVin(dto.vin());
        vehicle.setCondition(dto.condition());
        vehicle.setTransmission(dto.transmission());
        vehicle.setFuelType(dto.fuelType());
        vehicle.setBodyType(dto.bodyType());
        vehicle.setExteriorColor(dto.exteriorColor());
        vehicle.setInteriorColor(dto.interiorColor());
        vehicle.setDescription(dto.description());
        vehicle.setFeatured(dto.featured() != null ? dto.featured() : false);
        return vehicle;
    }

    public static void updateEntity(Vehicle vehicle, VehicleRequestDTO dto) {
        vehicle.setMake(dto.make());
        vehicle.setModel(dto.model());
        vehicle.setYear(dto.year());
        vehicle.setPrice(dto.price());
        vehicle.setMileage(dto.mileage());
        vehicle.setVin(dto.vin());
        vehicle.setCondition(dto.condition());
        vehicle.setTransmission(dto.transmission());
        vehicle.setFuelType(dto.fuelType());
        vehicle.setBodyType(dto.bodyType());
        vehicle.setExteriorColor(dto.exteriorColor());
        vehicle.setInteriorColor(dto.interiorColor());
        vehicle.setDescription(dto.description());
        vehicle.setFeatured(dto.featured() != null ? dto.featured() : vehicle.getFeatured());
    }

    public static VehicleResponseDTO toDTO(Vehicle vehicle){
        List<String> imageUrls = vehicle.getVehicleImages().stream()
                .map(VehicleImage::getImageUrl)
                .toList();

        return new VehicleResponseDTO(
                    vehicle.getId(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getPrice(),
                    vehicle.getMileage(),
                    vehicle.getVin(),
                    vehicle.getCondition(),
                    vehicle.getTransmission(),
                    vehicle.getFuelType(),
                    vehicle.getBodyType(),
                    vehicle.getExteriorColor(),
                    vehicle.getInteriorColor(),
                    vehicle.getDescription(),
                    vehicle.getFeatured(),
                    vehicle.getStatus(),
                    imageUrls,
                    vehicle.getCreatedAt(),
                    vehicle.getUpdatedAt()
                );
    }
}
