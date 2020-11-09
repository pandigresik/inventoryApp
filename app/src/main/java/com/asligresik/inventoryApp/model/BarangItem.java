package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

public class BarangItem {

    @SerializedName("koderak")
    private String koderak;

    @SerializedName("kodebarang")
    private String kodebarang;

    @SerializedName("quantity")
    private Float qty;

    public String getKoderak() {
        return koderak;
    }

    public void setKoderak(String koderak) {
        this.koderak = koderak;
    }

    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    public String getKodebarang() {
        return kodebarang;
    }

    public void setKodebarang(String kodebarang) {
        this.kodebarang = kodebarang;
    }

    @Override
    public String toString(){
        return
                String.format("StockItem{koderak = '%s',type = '%s',id = '%s',qty = '%d',tgl_transaksi = '%s'}", koderak, qty);
    }
}