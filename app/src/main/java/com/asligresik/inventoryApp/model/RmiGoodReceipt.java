package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RmiGoodReceipt {
    @SerializedName("rmi")
    @Expose
    private String rmi;

    @SerializedName("po")
    private List<String> po;

    @SerializedName("partnumber")
    private String partnumber;

    @SerializedName("partname")
    private String partname;

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

    @Override
    public String toString() {
        return "RmiGoodReceipt{" +
                "rmi='" + rmi + '\'' +
                ", po=" + po +
                ", partnumber='" + partnumber + '\'' +
                ", partname='" + partname + '\'' +
                '}';
    }
}