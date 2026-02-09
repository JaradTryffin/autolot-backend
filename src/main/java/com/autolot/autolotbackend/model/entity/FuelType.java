package com.autolot.autolotbackend.model.entity;

public enum FuelType {
    PETROL("Petrol"),
    DIESEL("Diesel"),
    ELECTRIC("Electric"),
    HYBRID("Hybrid");

    private String fuelTypeDisplayName;

    FuelType(String fuelTypeDisplayName){
        this.fuelTypeDisplayName = fuelTypeDisplayName;
    }

    public String getFuelTypeDisplayName(){
        return fuelTypeDisplayName;
    }
}
