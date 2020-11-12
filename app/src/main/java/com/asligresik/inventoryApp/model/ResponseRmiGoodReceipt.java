package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseRmiGoodReceipt {

	@SerializedName("content")
	private List<RmiGoodReceipt> items;

	@SerializedName("status")
	private Integer status;

	@SerializedName("message")
	private String message;

	public List<RmiGoodReceipt> getItems() {
		return items;
	}

	public void setItems(List<RmiGoodReceipt> items) {
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
				String.format("ResponseGoodReceipt{items = '%s',status = '%d',message = '%s'}", items, status, message);
	}
}