package net.cis.service;

import java.util.List;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.cis.bkav.entity.BkavResult;
import net.cis.bkav.entity.CommandDataEntity;
import net.cis.bkav.entity.Invoice;
import net.cis.bkav.entity.InvoiceAttachFileWS;
import net.cis.bkav.entity.InvoiceDetail;
import net.cis.bkav.entity.InvoiceDetailResult;
import net.cis.bkav.entity.InvoiceDetailsWSResult;
import net.cis.dto.BkavTicketDto;
import net.cis.dto.CompanyInforDto;
import net.cis.jpa.entity.EInvoiceEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */
public interface InvoiceService {
    
	String createInvoice(BkavTicketDto bkavTicketDto) throws Exception;
	
	boolean reCreateInvoice(EInvoiceEntity eInvoice) throws Exception;
	
	boolean editInvoice(BkavTicketDto bkavTicketDto) throws Exception;
    
	boolean cancelInvoice(String invoiceGUID) throws Exception;
    
	InvoiceDetailResult getInvoiceDetail(String invoiceGUID) throws Exception;
    
    BkavResult getInvoiceStatus(String invoiceGUID) throws Exception;
    
    int getInvoiceStatus(EInvoiceEntity invoice) throws Exception;
    
    BkavResult getInvoiceHistory(String invoiceGUID) throws Exception;
    
    Invoice prepareInvoiceData(BkavTicketDto bkavTicketDto);
    
    CompanyInforDto getCompanyInformationByTaxCode(String taxCode, long providerId) throws Exception;
    
    CommandDataEntity prepareDataForCreatingInvoice(BkavTicketDto bkavTicketDto) throws JsonProcessingException;
    
    CommandDataEntity prepareDataForGettingInvoiceDetail(String invoiceGUID);
    
    CommandDataEntity prepareDataForGettingInvoiceStatus(String invoiceGUID);
    
    CommandDataEntity prepareDataForEditInvoice(BkavTicketDto bkavTicketDto) throws Exception;
    
    CommandDataEntity prepareDataForGettingInvoiceHistory(String invoiceGUID);
    
    CommandDataEntity prepareDataForCancelingInvoice(String invoiceGUID) throws JsonProcessingException;
    
    CommandDataEntity prepareDataForGettingCompanyInformationByTaxCode(String taxCode);
    
    InvoiceDetail parseInvoiceDetailFromJson(String strInvoice) throws JSONException;
    
    List<InvoiceDetailsWSResult> parseListInvoiceDetailsWSFromJson(String strListInvoiceDetailsWS) throws JSONException;
    
    List<InvoiceAttachFileWS> parseListInvoiceAttachFileWSFromJson(String strListInvoiceAttachFileWS) throws JSONException;
}
 