package net.cis.jpa.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by NhanNguyen on 02/10/2018
 */
@Entity
@Table(name = "e_invoice")
public class EInvoiceEntity {

	@JsonProperty("ID")
	@Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@JsonProperty("TicketID")
	@Column(name="ticket_id")
	private String ticketId;
	
	@JsonProperty("TransactionID")
	@Column(name="transaction_id")
	private String transactionId;
	
	@JsonProperty("CarppId")
	@Column(name="carpp_id")
	private String cppId;
	
	@JsonProperty("TransactionAmount")
	@Column(name="transaction_amount")
	private double transactionAmount;
	
	@JsonProperty("CustomerEmail")
	@Column(name="customer_email")
	private String customerEmail;
	
	@JsonProperty("CustomerPhone")
	@Column(name="customer_phone")
	private String customerPhone;
	
	@JsonProperty("InvoiceStatus")
	@Column(name="invoice_status")
	private int invoiceStatus;
	
	@JsonProperty("ProvideId")
	@Column(name="provider_id")
	private int providerId;
	
	@JsonProperty("InvoiceGUID")
	@Column(name="invoice_guid")
	private String invoiceGUID;
	
	@JsonProperty("InvoiceCode")
	@Column(name = "invoice_code")
	private String invoiceCode;
	
	@JsonProperty("RequestBody")
	@Column(name = "request_body")
	private String requestBody;
	
	@JsonProperty("ResponseBody")
	@Column(name = "response_body")
	private String responseBody;
	
	@JsonProperty("PartnerInvoiceStringId")
	@Column(name = "partner_invoice_string_id")
	private String partnerInvoiceStringId;
	
	@JsonProperty("SystemStatus")
	@Column(name = "system_status")
	private String systemStatus;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
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

	public int getInvoiceStatus() {
		return invoiceStatus;
	}

	public void setInvoiceStatus(int invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	public String getInvoiceGUID() {
		return invoiceGUID;
	}

	public void setInvoiceGUID(String invoiceGUID) {
		this.invoiceGUID = invoiceGUID;
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

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public int getProviderId() {
		return providerId;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public String getPartnerInvoiceStringId() {
		return partnerInvoiceStringId;
	}

	public void setPartnerInvoiceStringId(String partnerInvoiceStringId) {
		this.partnerInvoiceStringId = partnerInvoiceStringId;
	}

	public String getSystemStatus() {
		return systemStatus;
	}

	public void setSystemStatus(String systemStatus) {
		this.systemStatus = systemStatus;
	}
	
}
