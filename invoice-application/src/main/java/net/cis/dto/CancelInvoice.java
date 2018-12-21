package net.cis.dto;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.cis.bkav.entity.InvoiceAttachFileWS;
import net.cis.bkav.entity.InvoiceDetailsWS;


/**
 * Created by NhanNguyen on 02/10/2018
 */

public class CancelInvoice {
	
	@JsonProperty("Invoice")
	private CancelInvoiceDto invoice;

	@JsonProperty("ListInvoiceDetailsWS")
	private List<InvoiceDetailsWS> listInvoiceDetailsWS;

	@JsonProperty("ListInvoiceAttachFileWS")
	private List<InvoiceAttachFileWS> listInvoiceAttachFileWS;
	
	@JsonProperty("PartnerInvoiceID")
	private long partnerInvoiceID;
	
	@JsonProperty("PartnerInvoiceStringID")
	private String partnerInvoiceStringID;
	
	public CancelInvoiceDto getInvoice() {
		return invoice;
	}
	public void setInvoice(CancelInvoiceDto invoice) {
		this.invoice = invoice;
	}
	public List<InvoiceDetailsWS> getListInvoiceDetailsWS() {
		return listInvoiceDetailsWS;
	}
	public void setListInvoiceDetailsWS(List<InvoiceDetailsWS> listInvoiceDetailsWS) {
		this.listInvoiceDetailsWS = listInvoiceDetailsWS;
	}
	public List<InvoiceAttachFileWS> getListInvoiceAttachFileWS() {
		return listInvoiceAttachFileWS;
	}
	public void setListInvoiceAttachFileWS(List<InvoiceAttachFileWS> listInvoiceAttachFileWS) {
		this.listInvoiceAttachFileWS = listInvoiceAttachFileWS;
	}
	public Long getPartnerInvoiceID() {
		return partnerInvoiceID;
	}
	public void setPartnerInvoiceID(long partnerInvoiceID) {
		this.partnerInvoiceID = partnerInvoiceID;
	}
	public String getPartnerInvoiceStringID() {
		return partnerInvoiceStringID;
	}
	public void setPartnerInvoiceStringID(String partnerInvoiceStringID) {
		this.partnerInvoiceStringID = partnerInvoiceStringID;
	}
	
}
