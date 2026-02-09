package com.autolot.autolotbackend.model.entity;

public enum VehicleStatus {
    AVAILABLE("Available"),
    SOLD("Sold"),
    PENDING("Pending");

    private String vehicleStatusDisplayName;

    VehicleStatus(String vehicleStatusDisplayName){
        this.vehicleStatusDisplayName = vehicleStatusDisplayName;
    }

    public String getVehicleStatusDisplayName(){
        return vehicleStatusDisplayName;
    }
}
