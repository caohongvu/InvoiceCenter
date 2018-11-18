package net.cis.jpa.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Created by NhanNguyen on 02/10/2018
 */
@Entity
@Table(name = "e_invoice")
public class BkavTicketEntity {
	@Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private double ticketAmount;
	private double paymentAmount;
	private String customerEmail;
	private int paymentVia;
	private int paymentApp;
	private String customerPhone;
	private int status;
	private Date createdAt;
	private int provideId;
	private String invoiceId;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getTicketAmount() {
		return ticketAmount;
	}

	public void setTicketAmount(double ticketAmount) {
		this.ticketAmount = ticketAmount;
	}

	public double getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(double paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public int getPaymentVia() {
		return paymentVia;
	}

	public void setPaymentVia(int paymentVia) {
		this.paymentVia = paymentVia;
	}

	public int getPaymentApp() {
		return paymentApp;
	}

	public void setPaymentApp(int paymentApp) {
		this.paymentApp = paymentApp;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getProvideId() {
		return provideId;
	}

	public void setProvideId(int provideId) {
		this.provideId = provideId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}	

}
