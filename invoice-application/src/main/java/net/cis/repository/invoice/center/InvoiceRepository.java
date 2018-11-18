package net.cis.repository.invoice.center;


import org.springframework.data.jpa.repository.JpaRepository;

import net.cis.jpa.entity.EInvoiceEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */

public interface InvoiceRepository  extends JpaRepository<EInvoiceEntity, Long> {
	
	

}
