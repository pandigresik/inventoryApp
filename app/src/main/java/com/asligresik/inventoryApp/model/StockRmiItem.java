package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

public class StockRmiItem {

    @SerializedName("rak")
    private String rak;

    @SerializedName("rmi")
    private String rmi;

    @SerializedName("qty")
    private Float qty;

    public String getRak() {
        return rak;
    }

    public void setRak(String rak) {
        this.rak = rak;
    }

    public String getRmi() {
        return rmi;
    }

    public void setRmi(String rmi) {
        this.rmi = rmi;
    }

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }
}