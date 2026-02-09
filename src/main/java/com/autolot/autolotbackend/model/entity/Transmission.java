package com.autolot.autolotbackend.model.entity;

public enum Transmission {
    AUTOMATIC("Automatic"),
    MANUAL("Manual");

    private String transmissionDisplayName;

    Transmission(String transmissionDisplayName){
        this.transmissionDisplayName = transmissionDisplayName;
    }

    public String getTransmissionDisplayName(){
        return transmissionDisplayName;
    }
}
