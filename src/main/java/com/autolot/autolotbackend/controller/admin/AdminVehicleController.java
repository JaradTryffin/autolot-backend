package com.autolot.autolotbackend.controller.admin;

import com.autolot.autolotbackend.model.dto.VehicleRequestDTO;
import com.autolot.autolotbackend.model.dto.VehicleResponseDTO;
import com.autolot.autolotbackend.service.vehicle.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vehicles")
@RequiredArgsConstructor
public class AdminVehicleController {
    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleRequestDTO vehicleRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(vehicleRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAll(){
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getById(@PathVariable String id){
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> update(@PathVariable String id, @RequestBody VehicleRequestDTO vehicle){
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
