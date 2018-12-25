package net.cis.repository.invoice.center;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.cis.jpa.entity.EInvoiceEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */
public interface EInvoiceRepository  extends JpaRepository<EInvoiceEntity, Long> {
	
	EInvoiceEntity findById(long id);
	
	EInvoiceEntity findByInvoiceGUID(String invoiceGUID);
	
	EInvoiceEntity findByPartnerInvoiceStringId(String partnerInvoiceStringId);
	
	List<EInvoiceEntity> findByTicketId(String ticketId);
	
	@Query(nativeQuery = true, value = "SELECT * from invoice_center.e_invoice where provider_id = :providerId order by invoice_no desc limit 0, 1")
	EInvoiceEntity findTopByProviderIdOrderByInvoiceNoDesc(@Param("providerId") int providerId);
	
	List<EInvoiceEntity> findByInvoiceStatusAndSystemStatus(int invoiceStatus, String systemStatus);

	EInvoiceEntity findDistinctByTicketIdAndTransactionId(String ticketId, String transactionId);
	
	List<EInvoiceEntity> findByInvoiceStatus(int invoiceStatus);
	
	
}
