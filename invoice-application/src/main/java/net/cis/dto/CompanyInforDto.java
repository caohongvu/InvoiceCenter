package net.cis.dto;


import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by NhanNguyen on 02/10/2018
 */
@Entity
public class CompanyInforDto {
	
	@JsonProperty("TaxCode")
	private String taxCode;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("PrimaryAddress")
	private String primaryAddress;
	
	@JsonProperty("AlternativeAddress")
	private String alternativeAddress;
	
	@JsonProperty("OperationStatus")
	private String operationStatus;

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPrimaryAddress() {
		return primaryAddress;
	}

	public void setPrimaryAddress(String primaryAddress) {
		this.primaryAddress = primaryAddress;
	}

	public String getAlternativeAddress() {
		return alternativeAddress;
	}

	public void setAlternativeAddress(String alternativeAddress) {
		this.alternativeAddress = alternativeAddress;
	}

	public String getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}
	
}
