package com.autolot.autolotbackend.controller.admin;

import com.autolot.autolotbackend.model.entity.Vehicle;
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
    public ResponseEntity<Vehicle> create(@RequestBody Vehicle vehicle){
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.createVehicle(vehicle));
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAll(){
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getById(@PathVariable String id){
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> update(@PathVariable String id, @RequestBody Vehicle vehicle){
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
