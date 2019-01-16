package net.cis.dto;

import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.cis.jpa.entity.PaymentConfig;

/**
 * Created by NhanNguyen on 19/10/2018
 */
public class BkavTicketDto {
	
	@JsonProperty("TicketID")
	private String ticketId;
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("TransactionAmount")
	private double transactionAmount;
	
	@JsonProperty("CarppID")
	private String cppId;
	
	@JsonProperty("ProviderID")
	private int providerId;
	
	@JsonProperty("BuyerName")
	private String buyerName;
	
	@JsonProperty("BuyerTaxCode")
	private String buyerTaxCode;
	
	@JsonProperty("BuyerUnitName")
	private String buyerUnitName;
	
	@JsonProperty("BuyerAddress")
	private String buyerAddress;
	
	@JsonProperty("BuyerBankAccount")
	private String buyerBankAccount;
	
	@JsonProperty("ReceiverEmail")
	private String receiverEmail;
	
	@JsonProperty("ReceiverMobile")
	private String receiverMobile;
	
	@JsonProperty("ReceiverAddress")
	private String receiverAddress;
	
	@JsonProperty("ReceiverName")
	private String receiverName;
	
	@JsonProperty("CppCode")
	private String cppCode;
	
	@JsonProperty("TransactionID")
	private String transactionId;
	
	@JsonProperty("IsMonthly")
	private int isMonthly;
	
	@JsonProperty("PaymentConfiguration")
	private List<PaymentConfig> paymentConfiguration;
	
	@JsonProperty("PartnerInvoiceStringId")
	@Column(name = "partner_invoice_string_id")
	private String partnerInvoiceStringId;
	
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerTaxCode() {
		return buyerTaxCode;
	}
	public void setBuyerTaxCode(String buyerTaxCode) {
		this.buyerTaxCode = buyerTaxCode;
	}
	public String getBuyerUnitName() {
		return buyerUnitName;
	}
	public void setBuyerUnitName(String buyerUnitName) {
		this.buyerUnitName = buyerUnitName;
	}
	public String getBuyerAddress() {
		return buyerAddress;
	}
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}
	public String getBuyerBankAccount() {
		return buyerBankAccount;
	}
	public void setBuyerBankAccount(String buyerBankAccount) {
		this.buyerBankAccount = buyerBankAccount;
	}
	public String getReceiverEmail() {
		return receiverEmail;
	}
	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}
	public String getReceiverMobile() {
		return receiverMobile;
	}
	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
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
	public String getCppId() {
		return cppId;
	}
	public void setCppId(String cppId) {
		this.cppId = cppId;
	}
	public int getProviderId() {
		return providerId;
	}
	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}
	public String getCppCode() {
		return cppCode;
	}
	public void setCppCode(String cppCode) {
		this.cppCode = cppCode;
	}
	public List<PaymentConfig> getPaymentConfiguration() {
		return paymentConfiguration;
	}
	public void setPaymentConfiguration(List<PaymentConfig> paymentConfiguration) {
		this.paymentConfiguration = paymentConfiguration;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public int getIsMonthly() {
		return isMonthly;
	}
	public void setIsMonthly(int isMonthly) {
		this.isMonthly = isMonthly;
	}
	public String getPartnerInvoiceStringId() {
		return partnerInvoiceStringId;
	}

	public void setPartnerInvoiceStringId(String partnerInvoiceStringId) {
		this.partnerInvoiceStringId = partnerInvoiceStringId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
