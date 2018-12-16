package net.cis.dto;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by NhanNguyen on 02/10/2018
 */

public class EInvoiceDto {
	
	@JsonProperty("TicketId")
	private String ticketId;
	
	@JsonProperty("TransactionId")
	private String transactionId;
	
	@JsonProperty("CarppId")
	private String cppId;
	
	@JsonProperty("CustomerEmail")
	private String customerEmail;
	
	@JsonProperty("CustomerPhone")
	private String customerPhone;

	@JsonProperty("InvoiceCode")
	private String invoiceCode;
	
	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCppId() {
		return cppId;
	}

	public void setCppId(String cppId) {
		this.cppId = cppId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}
	
}
