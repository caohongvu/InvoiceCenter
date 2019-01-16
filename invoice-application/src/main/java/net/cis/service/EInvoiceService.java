package net.cis.service;


import java.util.List;

import net.cis.dto.BkavTicketDto;
import net.cis.jpa.entity.EInvoiceEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */
public interface EInvoiceService {

	long createEInvoice(BkavTicketDto bkavTicketDto);
    
    void updateEInvoice(long id, int invoiceStatus, String invoiceGUID, String invoiceCode, String requestBody, String responseBody);
    
    void updateEInvoiceStatus(long id, int invoiceStatus);
    
    void updateEInvoiceSystemStatus(long id, String systemStatus);
    
    List<EInvoiceEntity> getByTicketId(String ticketId);
    
    EInvoiceEntity getByInvoiceGUID(String invoiceGUID);
    
    List<EInvoiceEntity> getInvoiceFailed(int providerId, String email, String phone);
    
    EInvoiceEntity getByPartnerStringId(String partnerStringId);
    
    EInvoiceEntity getByTicketIdAndTranId(String ticketId, String tranId);
    
    
    
}
 