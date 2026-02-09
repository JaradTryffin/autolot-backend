package com.autolot.autolotbackend.model.entity;

public enum VehicleCondition {
    NEW("New"),
    USED("Used"),
    CERTIFIED_PRE_OWNED("Certified Pre-Owned");

    private String vehicleConditionName;

    VehicleCondition(String vehicleConditionName){
        this.vehicleConditionName = vehicleConditionName;
    }

    public String getVehicleConditionName(){
        return vehicleConditionName;
    }
}
