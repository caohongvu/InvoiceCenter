package net.cis.jpa.entity;

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
@Table(name = "company_key")
public class CompanyKeyEntity {
	@Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="partner_token")
	private String partnerToken;
	
	@Column(name="partner_guid")
	private String partnerGuid;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPartnerToken() {
		return partnerToken;
	}

	public void setPartnerToken(String partnerToken) {
		this.partnerToken = partnerToken;
	}

	public String getPartnerGuid() {
		return partnerGuid;
	}

	public void setPartnerGuid(String partnerGuid) {
		this.partnerGuid = partnerGuid;
	}
	
}
