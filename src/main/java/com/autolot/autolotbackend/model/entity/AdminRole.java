package com.autolot.autolotbackend.model.entity;

public enum AdminRole {
    OWNER("Owner"),
    ADMIN("Admin");

    private String displayName;

    AdminRole(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

}
