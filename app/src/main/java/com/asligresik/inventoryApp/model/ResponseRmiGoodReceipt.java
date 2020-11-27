package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class ResponseRmiGoodReceipt {

	@SerializedName("content")
	private HashMap<String,PartNameGoodReceipt> items;

	@SerializedName("status")
	private Integer status;

	@SerializedName("message")
	private String message;

	public HashMap<String, PartNameGoodReceipt> getItems() {
		return items;
	}

	public void setItems(HashMap<String, PartNameGoodReceipt> items) {
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
	public String toString() {
		return "ResponseRmiGoodReceipt{" +
				"items=" + items +
				", status=" + status +
				", message='" + message + '\'' +
				'}';
	}
}