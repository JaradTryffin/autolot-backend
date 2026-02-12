package com.autolot.autolotbackend.tenant;

public class TenantContext {
    private static final ThreadLocal<String> dealershipId = new ThreadLocal<>();

    public static void setTenantId(String id){
        dealershipId.set(id);
    }

    public static String getTenantId(){
        return dealershipId.get();
    }

    public static void clear(){
        dealershipId.remove();
    }
}
