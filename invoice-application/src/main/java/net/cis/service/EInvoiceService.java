package net.cis.service;


import java.util.List;

import net.cis.dto.BkavTicketDto;
import net.cis.jpa.entity.EInvoiceEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */
public interface EInvoiceService {

	long createEInvoice(BkavTicketDto bkavTicketDto);
    
    void updateEInvoice(long id, int invoiceStatus, String invoiceGUID, String invoiceCode);
    
    List<EInvoiceEntity> getByTicketId(String ticketId);
    
    EInvoiceEntity getByInvoiceGUID(String invoiceGUID);
    
}
 