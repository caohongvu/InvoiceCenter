package net.cis.dto;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by NhanNguyen on 02/10/2018
 */

public class CancelInvoiceDto {
	
	@JsonProperty("InvoiceGUID")
	private String invoiceGuid;

	public String getInvoiceGuid() {
		return invoiceGuid;
	}

	public void setInvoiceGuid(String invoiceGuid) {
		this.invoiceGuid = invoiceGuid;
	}
}
