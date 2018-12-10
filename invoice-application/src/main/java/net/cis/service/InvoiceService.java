package net.cis.service;

import java.util.List;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.cis.bkav.entity.BkavResult;
import net.cis.bkav.entity.CommandDataEntity;
import net.cis.bkav.entity.InvoiceAttachFileWS;
import net.cis.bkav.entity.InvoiceDetail;
import net.cis.bkav.entity.InvoiceDetailsWSResult;
import net.cis.dto.BkavTicketDto;
import net.cis.dto.CompanyInforDto;

/**
 * Created by NhanNguyen 19/10/2018
 */
public interface InvoiceService {
    
    BkavResult createInvoice(BkavTicketDto bkavTicketDto) throws Exception;
    
    BkavResult getInvoiceDetail(String invoiceGUID) throws Exception;
    
    BkavResult getInvoiceStatus(String invoiceGUID) throws Exception;
    
    BkavResult getInvoiceHistory(String invoiceGUID) throws Exception;
    
    CompanyInforDto getCompanyInformationByTaxCode(String taxCode, long providerId) throws Exception;
    
    CommandDataEntity prepareDataForCreatingInvoice(BkavTicketDto bkavTicketDto) throws JsonProcessingException;
    
    CommandDataEntity prepareDataForGettingInvoiceDetail(String invoiceGUID);
    
    CommandDataEntity prepareDataForGettingInvoiceStatus(String invoiceGUID);
    
    CommandDataEntity prepareDataForGettingInvoiceHistory(String invoiceGUID);
    
    CommandDataEntity prepareDataForGettingCompanyInformationByTaxCode(String taxCode);
    
    InvoiceDetail parseInvoiceDetailFromJson(String strInvoice) throws JSONException;
    
    List<InvoiceDetailsWSResult> parseListInvoiceDetailsWSFromJson(String strListInvoiceDetailsWS) throws JSONException;
    
    List<InvoiceAttachFileWS> parseListInvoiceAttachFileWSFromJson(String strListInvoiceAttachFileWS) throws JSONException;
}
 