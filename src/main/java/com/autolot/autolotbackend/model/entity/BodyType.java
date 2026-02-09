package com.autolot.autolotbackend.model.entity;

public enum BodyType {
    SEDAN("Sedan"),
    SUV("Suv"),
    TRUCK("Truck"),
    COUPE("Coupe"),
    HATCHBACK("HatchBack"),
    VAN("Van"),
    CONVERTIBLE("Convertible");

    private String bodyTypeDisplayName;

    BodyType(String bodyTypeDisplayName){
        this.bodyTypeDisplayName = bodyTypeDisplayName;
    }

    public String getBodyTypeDisplayName(){
        return bodyTypeDisplayName;
    }
}
