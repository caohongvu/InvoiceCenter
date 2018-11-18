package net.cis.repository.invoice.center;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.cis.jpa.entity.EInvoiceEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */

public interface EInvoiceRepository  extends JpaRepository<EInvoiceEntity, Long> {
	
	EInvoiceEntity findById(long id);
	
	EInvoiceEntity findByInvoiceGUID(String invoiceGUID);
	
	List<EInvoiceEntity> findByTicketId(String ticketId);

}
