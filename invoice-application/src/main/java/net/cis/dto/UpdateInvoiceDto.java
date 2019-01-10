package net.cis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by NhanNguyen on 19/10/2018
 */
public class UpdateInvoiceDto {
	
	@JsonProperty("TicketId")
	private String ticketId;
	
	@JsonProperty("TransactionId")
	private String transactionId;
	
	@JsonProperty("InvoiceCode")
	private String invoiceCode;
	
	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
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
