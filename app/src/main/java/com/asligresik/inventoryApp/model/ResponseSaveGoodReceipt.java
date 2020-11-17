package com.asligresik.inventoryApp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseSaveGoodReceipt {

	@SerializedName("content")
	private String printText;

	@SerializedName("status")
	private Integer status;

	@SerializedName("message")
	private String message;

	@SerializedName("label")
	private List<String> label;

	public String getPrintText() {
		return printText;
	}

	public void setPrintText(String printText) {
		this.printText = printText;
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

	public List<String> getLabel() {
		return label;
	}

	public void setLabel(List<String> label) {
		this.label = label;
	}

	@Override
	public String toString(){
		return
				String.format("ResponseStock{printText = '%s',status = '%d',message = '%s'}", printText, status, message);
	}
}