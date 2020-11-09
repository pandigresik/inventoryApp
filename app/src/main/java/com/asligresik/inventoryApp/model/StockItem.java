package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

public class StockItem {

	@SerializedName("koderak")
	private String koderak;

	@SerializedName("id")
	private String id;

	@SerializedName("kodebarang")
    private String kodebarang;

	@SerializedName("qty")
	private Integer qty;

	public String getKoderak() {
		return koderak;
	}

	public void setKoderak(String koderak) {
		this.koderak = koderak;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
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
				String.format("StockItem{koderak = '%s',id = '%s',qty = '%d'}", koderak, id, qty);
	}
}