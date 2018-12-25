package net.cis.dto;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by NhanNguyen on 02/10/2018
 */

public class FailedInvoice {
	
	@JsonProperty("ProviderId")
	private int providerId;

	@JsonProperty("CarppId")
	private String carppId;

	@JsonProperty("Email")
	private String email;
	
	@JsonProperty("Phone")
	private String phone;

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public String getCarppId() {
		return carppId;
	}

	public void setCarppId(String carppId) {
		this.carppId = carppId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
