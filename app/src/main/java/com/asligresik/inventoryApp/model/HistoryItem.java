package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

public class HistoryItem {

    @SerializedName("id")
    private String id;

    @SerializedName("koderak")
    private String koderak;

    @SerializedName("kodebarang")
    private String kodebarang;

    @SerializedName("type")
    private String type;

    @SerializedName("qty")
    private Integer qty;

    @SerializedName("tgl_transaksi")
    private String tgl_transaksi;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKoderak() {
        return koderak;
    }

    public void setKoderak(String koderak) {
        this.koderak = koderak;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getTgl_transaksi() {
        return tgl_transaksi;
    }

    public void setTgl_transaksi(String tgl_transaksi) {
        this.tgl_transaksi = tgl_transaksi;
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
                String.format("StockItem{koderak = '%s',type = '%s',id = '%s',qty = '%d',tgl_transaksi = '%s'}", koderak, type, id, qty, tgl_transaksi);
    }
}