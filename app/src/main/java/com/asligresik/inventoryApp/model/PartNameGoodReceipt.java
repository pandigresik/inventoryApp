package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartNameGoodReceipt {
    @SerializedName("rmi")
    @Expose
    private LinkedTreeMap<String, RmiGoodReceipt> rmi;

    public PartNameGoodReceipt(){
        this.rmi = new LinkedTreeMap<>();
    }

    public LinkedTreeMap<String, RmiGoodReceipt> getRmi() {
        return rmi;
    }

    public void setRmi(LinkedTreeMap<String, RmiGoodReceipt> rmi) {
        this.rmi = rmi;
    }

    public List<String> getKeyRmi(){
        List<String> result = new ArrayList<String>();
        rmi.forEach((k, v) -> {
            result.add(k);
        });
        return result;
    }

    @Override
    public String toString() {
        return "PartNameGoodReceipt{" +
                "rmi=" + rmi +
                '}';
    }
}