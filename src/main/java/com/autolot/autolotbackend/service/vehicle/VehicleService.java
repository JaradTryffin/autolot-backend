package com.autolot.autolotbackend.service.vehicle;

import com.autolot.autolotbackend.mapper.VehicleMapper;
import com.autolot.autolotbackend.model.dto.VehicleRequestDTO;
import com.autolot.autolotbackend.model.dto.VehicleResponseDTO;
import com.autolot.autolotbackend.model.entity.Dealership;
import com.autolot.autolotbackend.model.entity.Vehicle;
import com.autolot.autolotbackend.model.entity.VehicleStatus;
import com.autolot.autolotbackend.repository.DealershipRepository;
import com.autolot.autolotbackend.repository.VehicleRepository;
import com.autolot.autolotbackend.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    @PersistenceContext
    private EntityManager entityManager;

    private final VehicleRepository vehicleRepository;
    private final DealershipRepository dealershipRepository;

    @Transactional
    public VehicleResponseDTO createVehicle(VehicleRequestDTO vehicleRequestDTO){
        String dealershipId = TenantContext.getDealershipId();
        Dealership dealership = dealershipRepository.findById(dealershipId)
                .orElseThrow(() -> new RuntimeException("Dealership not found"));

        Vehicle vehicle = VehicleMapper.toEntity(vehicleRequestDTO);
        vehicle.setDealership(dealership);
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        return VehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    @Transactional
    public List<VehicleResponseDTO> getAllVehicles(){
        Session session = entityManager.unwrap(Session.class);

        session.enableFilter("tenant_id")
                .setParameter("tenant", TenantContext.getDealershipId());

        return vehicleRepository.findAll().stream()
                .map(VehicleMapper::toDTO)
                .toList();
    }

    public VehicleResponseDTO getVehicleById(String id){
        return VehicleMapper.toDTO(vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found")));
    }

    @Transactional
    public VehicleResponseDTO updateVehicle(String id, VehicleRequestDTO vehicleResponseDTO){
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        VehicleMapper.updateEntity(vehicle, vehicleResponseDTO);
        return VehicleMapper.toDTO(vehicleRepository.save(vehicle));
    }

    public void deleteVehicle(String id){
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        vehicleRepository.delete(vehicle);
    }
}
