package net.cis.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentConfig {
	
	@JsonProperty("ItemName")
	private String itemName;
	
	@JsonProperty("Price")
	private int price;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	
}
