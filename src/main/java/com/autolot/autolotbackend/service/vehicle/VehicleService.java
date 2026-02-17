package com.autolot.autolotbackend.service.vehicle;

import com.autolot.autolotbackend.model.entity.Dealership;
import com.autolot.autolotbackend.model.entity.Vehicle;
import com.autolot.autolotbackend.model.entity.VehicleStatus;
import com.autolot.autolotbackend.repository.DealershipRepository;
import com.autolot.autolotbackend.repository.VehicleRepository;
import com.autolot.autolotbackend.tenant.TenantContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final DealershipRepository dealershipRepository;

    @Transactional
    public Vehicle createVehicle(Vehicle vehicle){
        String dealershipId = TenantContext.getDealershipId();
        Dealership dealership = dealershipRepository.findById(dealershipId)
                .orElseThrow(() -> new RuntimeException("Dealership not found"));

        vehicle.setDealership(dealership);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles(){
        // For now, fetch all - later the Hibernate tenant filter
        // will auto-scope this to the current dealership
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(String id){
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    @Transactional
    public Vehicle updateVehicle(String id, Vehicle updatedVehicle){
        Vehicle existing = getVehicleById(id);
        existing.setMake(updatedVehicle.getMake());
        existing.setModel(updatedVehicle.getModel());
        existing.setYear(updatedVehicle.getYear());
        existing.setPrice(updatedVehicle.getPrice());
        existing.setMileage(updatedVehicle.getMileage());
        existing.setCondition(updatedVehicle.getCondition());
        existing.setTransmission(updatedVehicle.getTransmission());
        existing.setFuelType(updatedVehicle.getFuelType());
        existing.setBodyType(updatedVehicle.getBodyType());
        existing.setExteriorColor(updatedVehicle.getExteriorColor());
        existing.setInteriorColor(updatedVehicle.getInteriorColor());
        existing.setDescription(updatedVehicle.getDescription());
        existing.setFeatured(updatedVehicle.getFeatured());
        existing.setStatus(updatedVehicle.getStatus());
        return vehicleRepository.save(existing);
    }

    public void deleteVehicle(String id){
        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }
}
