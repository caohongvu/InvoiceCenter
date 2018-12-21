package net.cis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by NhanNguyen on 19/10/2018
 */
public class BkavCancelDto {
	
	@JsonProperty("TicketID")
	private String ticketId;
	
	@JsonProperty("TransactionID")
	private String transactionId;

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
	
}
