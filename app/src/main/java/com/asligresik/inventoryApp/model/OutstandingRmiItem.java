package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

public class OutstandingRmiItem {
    @SerializedName("rmi")
    private String rmi;

    @SerializedName("number")
    private String number;

    @SerializedName("name")
    private String name;

    @SerializedName("namefg")
    private String namefg;

    @SerializedName("qty")
    private Integer qty;

    @SerializedName("qtyact")
    private Integer qtyact;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamefg() {
        return namefg;
    }

    public void setNamefg(String namefg) {
        this.namefg = namefg;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getQtyact() {
        return qtyact;
    }

    public void setQtyact(Integer qtyact) {
        this.qtyact = qtyact;
    }
}