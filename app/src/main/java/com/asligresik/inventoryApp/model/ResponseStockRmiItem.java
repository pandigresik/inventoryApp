package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseStockRmiItem {

	@SerializedName("content")
	private List<StockRmiItem> items;

	@SerializedName("status")
	private Integer status;

	@SerializedName("total")
	private Integer total;

	@SerializedName("message")
	private String message;

	public List<StockRmiItem> getItems() {
		return items;
	}

	public void setItems(List<StockRmiItem> items) {
		this.items = items;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString(){
		return
				String.format("ResponseStock{items = '%s',status = '%d',message = '%s'}", items, status, message);
	}
}