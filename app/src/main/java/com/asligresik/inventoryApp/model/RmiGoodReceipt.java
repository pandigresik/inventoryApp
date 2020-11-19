package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RmiGoodReceipt {
    @SerializedName("rmi")
    private String rmi;

    @SerializedName("po")
    private List<String> po;

    public String getPartnumber() {
        return partnumber;
    }

    public void setPartnumber(String partnumber) {
        this.partnumber = partnumber;
    }

    public String getPartname() {
        return partname;
    }

    public void setPartname(String partname) {
        this.partname = partname;
    }

    @SerializedName("partnumber")
    private String partnumber;

    @SerializedName("partname")
    private String partname;

    public String getRmi() {
        return rmi;
    }

    public void setRmi(String rmi) {
        this.rmi = rmi;
    }

    public List<String> getPo() {
        return po;
    }

    public void setPo(List<String> po) {
        this.po = po;
    }
}