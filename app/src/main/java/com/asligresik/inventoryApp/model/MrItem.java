package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

public class MrItem {
    @SerializedName("mr")
    private String mr;

    @SerializedName("fg")
    private String fg;

    public String getMr() {
        return mr;
    }

    public void setMr(String mr) {
        this.mr = mr;
    }

    public String getFg() {
        return fg;
    }

    public void setFg(String fg) {
        this.fg = fg;
    }

    public String getMrFg() {
        return mr.concat("__").concat(fg);
    }

    @Override
    public String toString() {
        return "MrItem{" +
                "mr='" + mr + '\'' +
                ", fg='" + fg + '\'' +
                '}';
    }
}