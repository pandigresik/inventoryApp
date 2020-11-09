package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

public class RmiItem {
    @SerializedName("rmi")
    private String rmi;

    @SerializedName("number")
    private String number;

    @SerializedName("tanggal")
    private String tanggal;

    public String getRmi() {
        return rmi;
    }

    public void setRmi(String rmi) {
        this.rmi = rmi;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setName(String tanggal) {
        this.tanggal = tanggal;
    }
}