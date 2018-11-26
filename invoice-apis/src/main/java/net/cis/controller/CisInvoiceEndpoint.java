package net.cis.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import net.cis.bkav.entity.BkavResult;
import net.cis.bkav.entity.BkavSuccess;
import net.cis.bkav.entity.InvoiceSearchResult;
import net.cis.common.util.constant.BkavConfigurationConstant;
import net.cis.common.web.BaseEndpoint;
import net.cis.common.web.ResponseError;
import net.cis.dto.BkavTicketDto;
import net.cis.dto.EInvoiceDto;
import net.cis.dto.ResponseDto;
import net.cis.jpa.entity.EInvoiceEntity;
import net.cis.service.CompanyKeyService;
import net.cis.service.EInvoiceService;
import net.cis.service.InvoiceService;

/**
 * Created by NhanNguyen on 19/10/2018
 */
@RestController
@RequestMapping("/e_invoices")
@Api(value = "Invoice API Endpoint", description = "Invoice Data Entities Endpoint")
public class CisInvoiceEndpoint extends BaseEndpoint {

	@Autowired
	InvoiceService invoiceService;
	
	@Autowired
	EInvoiceService eInvoiceService;
	
	@Autowired
	CompanyKeyService companyKeyService;
	
	@RequestMapping(value = "/create", method = RequestMethod.POST) 
	@ResponseBody
	public ResponseDto createInvoice(HttpServletRequest request, @RequestBody BkavTicketDto bkavTicketDto) throws Exception {
		// Create eInvoice data
	 	long id = eInvoiceService.createEInvoice(bkavTicketDto);
	 	ResponseDto response = new ResponseDto();

		// Call BKAV to create Invoice
		BkavResult bkavResult = new BkavResult();	
		bkavResult = invoiceService.createInvoice(bkavTicketDto);
		
		if (bkavResult.getStatus() == 1) {
			response.setError(new ResponseError(HttpServletResponse.SC_BAD_REQUEST, bkavResult.getResult().toString()));
			return response;
		}
		// Update Invoice GUID for ticket
		@SuppressWarnings("unchecked")
		List<BkavSuccess> list = (List<BkavSuccess>) bkavResult.getResult();
		for (BkavSuccess item : list) {
			eInvoiceService.updateEInvoice(id, item.getStatus(), item.getInvoiceGUID(), item.getInvoiceCode());
			response.setData(item.getInvoiceCode());
		}
		response.setError(new ResponseError(HttpServletResponse.SC_OK, ""));
		return response;
	}
	
	@RequestMapping(value = "/detail/", method = RequestMethod.GET)
	@ResponseBody
	public BkavResult getInvoiceDetail(HttpServletRequest request, @RequestParam("invoiceGUID") String invoiceGUID) throws Exception {
		
		BkavResult bkavResult = new BkavResult();
		bkavResult = invoiceService.getInvoiceDetail(invoiceGUID);
		
		return bkavResult;
	}
	
	@RequestMapping(value = "/detail/by_ticket", method = RequestMethod.GET)
	@ResponseBody
	public ResponseDto getInvoiceForDownloading(HttpServletRequest request, @RequestParam("ticketId") String ticketId) throws Exception {
		InvoiceSearchResult invoiceSearchResult = new InvoiceSearchResult();
		List<String> invoiceCodes = new ArrayList<String>();
		
		List<EInvoiceEntity> eInvoiceEntitys = eInvoiceService.getByTicketId(ticketId);
		for (EInvoiceEntity eInvoiceEntity : eInvoiceEntitys) {
				String invoiceCode = eInvoiceEntity.getInvoiceCode();
				if (!invoiceCode.equals("") && !invoiceCode.isEmpty()) {
					invoiceCodes.add(invoiceCode);
				}
		}
		
		invoiceSearchResult.setInvoiceCodes(invoiceCodes);
		invoiceSearchResult.setUrl(BkavConfigurationConstant.BKAV_DOWNLOAD_INVOICE_URL);
		
		ResponseDto response = new ResponseDto();
		response.setError(new ResponseError(HttpServletResponse.SC_OK, ""));
		response.setData(invoiceSearchResult);
		
		return response;
	}
	
	@RequestMapping(value = "/status/", method = RequestMethod.GET) 
	@ResponseBody
	public BkavResult getInvoiceStatus(HttpServletRequest request, @RequestParam("invoiceGUID") String invoiceGUID) throws Exception {
	
		BkavResult bkavResult = new BkavResult();
		bkavResult = invoiceService.getInvoiceStatus(invoiceGUID);
		
		return bkavResult;
	}
	
	@RequestMapping(value = "/history/", method = RequestMethod.GET)
	@ResponseBody
	public BkavResult getInvoiceHistory(HttpServletRequest request, @RequestParam("invoiceGUID") String invoiceGUID) throws Exception {
		
		BkavResult bkavResult = new BkavResult();
		bkavResult = invoiceService.getInvoiceHistory(invoiceGUID);
		
		return bkavResult;
	}
	
	@RequestMapping(value = "/company_infor/by_tax_code", method = { RequestMethod.GET })
	@ResponseBody
	public BkavResult getCompanyByTaxCode(HttpServletRequest request, @RequestParam("tax_code") String taxCode) throws Exception {
		
		BkavResult bkavResult = new BkavResult();
		bkavResult = invoiceService.getCompanyInformationByTaxCode(taxCode);
		
		return bkavResult;
	}
	
	@RequestMapping(value = "/list/", method = { RequestMethod.GET })
	@ResponseBody
	public ResponseDto getListEInvoice(HttpServletRequest request, @RequestParam("ticketId") String ticketId) throws Exception {
		List<EInvoiceDto> list = new ArrayList<EInvoiceDto>();
		
		ResponseDto response = new ResponseDto();
		
		List<EInvoiceEntity> eInvoiceEntitys = eInvoiceService.getByTicketId(ticketId);
		if (eInvoiceEntitys.size() == 0) {
			response.setError(new ResponseError(HttpServletResponse.SC_OK, "Ticket không tồn tại"));
			response.setData("");
			return response;
		}
		for (EInvoiceEntity eInvoiceEntity : eInvoiceEntitys) {
			// Get invoice code
			String invoiceCode = eInvoiceEntity.getInvoiceCode();
			if (!invoiceCode.equals("") && !invoiceCode.isEmpty()) {
				
				EInvoiceDto eInvoiceDto = new EInvoiceDto();
				eInvoiceDto.setInvoiceCode(invoiceCode);
				eInvoiceDto.setTicketId(ticketId);
				eInvoiceDto.setTransactionId(eInvoiceEntity.getTransactionId());
				eInvoiceDto.setCustomerEmail(eInvoiceEntity.getCustomerEmail());
				eInvoiceDto.setCustomerPhone(eInvoiceEntity.getCustomerPhone());
				eInvoiceDto.setCppId(eInvoiceEntity.getCppId());
				
				list.add(eInvoiceDto);
			}
		}
		
		response.setError(new ResponseError(HttpServletResponse.SC_OK, ""));
		response.setData(list);
		
		return response;
	}
	
	@RequestMapping(value = "/detail/update/", method = RequestMethod.GET)
	@ResponseBody
	public ResponseDto getInvoiceFullObject(HttpServletRequest request, @RequestParam("ticketId") String ticketId) throws Exception {
		ResponseDto response = new ResponseDto();
		
		List<EInvoiceEntity> eInvoiceEntitys = eInvoiceService.getByTicketId(ticketId);
		if (eInvoiceEntitys.size() == 0) {
			response.setError(new ResponseError(HttpServletResponse.SC_OK, "Ticket không tồn tại"));
			response.setData("");
			return response;
		}
		
		response.setError(new ResponseError(HttpServletResponse.SC_OK, ""));
		response.setData(eInvoiceEntitys);
		
		return response;
	}
}
