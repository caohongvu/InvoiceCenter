package net.cis.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.cis.bkav.BkavExecCommand;
import net.cis.bkav.entity.BkavRequest;
import net.cis.bkav.entity.BkavResponse;
import net.cis.bkav.entity.BkavResult;
import net.cis.bkav.entity.CommandDataEntity;
import net.cis.bkav.entity.CommandObject;
import net.cis.bkav.entity.Invoice;
import net.cis.bkav.entity.InvoiceAttachFileWS;
import net.cis.bkav.entity.InvoiceDetail;
import net.cis.bkav.entity.InvoiceDetailResult;
import net.cis.bkav.entity.InvoiceDetailsWS;
import net.cis.bkav.entity.InvoiceDetailsWSResult;
import net.cis.bkav.entity.InvoiceHistory;
import net.cis.common.util.BkavInvoiceUtil;
import net.cis.common.util.constant.BkavConfigurationConstant;
import net.cis.common.util.constant.BkavTaxRateConstant;
import net.cis.common.util.constant.InvoiceCmdType;
import net.cis.common.util.constant.ReceiveInvoiceTypeConstant;
import net.cis.dto.BkavTicketDto;
import net.cis.dto.CancelInvoice;
import net.cis.dto.CancelInvoiceDto;
import net.cis.dto.CompanyInforDto;
import net.cis.jpa.entity.CompanyKeyEntity;
import net.cis.jpa.entity.ConfigurationEntity;
import net.cis.jpa.entity.EInvoiceEntity;
import net.cis.jpa.entity.PaymentConfig;
import net.cis.service.CompanyKeyService;
import net.cis.service.CompressionService;
import net.cis.service.ConfigurationService;
import net.cis.service.EInvoiceService;
import net.cis.service.EncryptionService;
import net.cis.service.InvoiceService;
import net.cis.service.cache.ConfigurationCache;
/**
 * Created by NhanNguyen on 19/10/2018
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {

	ModelMapper mapper;
	
	@Autowired
	BkavExecCommand bkavExecCommand;
	
	@Autowired
	CompressionService compressionService;
	
	@Autowired
	EncryptionService encryptionService;
	
	@Autowired
	EInvoiceService eInvoiceService;
	
	@Autowired
	CompanyKeyService companyKeyService;
	
	@Autowired
	ConfigurationService configurationService;
	
	@Autowired
	ConfigurationCache configurationCache;
	
	@PostConstruct
	public void initialize() {
		mapper = new ModelMapper();
		
		ConfigurationEntity entity = configurationService.getWebService("url");
		configurationCache.put("URL", entity.getValue());
	}

	@Override
	public String createInvoice(BkavTicketDto bkavTicketDto) throws Exception {
		String invoiceCode = "";
		bkavTicketDto.setPartnerInvoiceStringId(BkavInvoiceUtil.generateGuidString());
		long id = eInvoiceService.createEInvoice(bkavTicketDto);
		
		CommandDataEntity commandDataEntity = prepareDataForCreatingInvoice(bkavTicketDto);

		int companyId = bkavTicketDto.getProviderId();
	 	CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(companyId);
	 	String encryptedCommandData = encryptionService.doEncryptedCommandData(commandDataEntity, companyKeyEntity.getPartnerToken());
		
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		// Call BKAV Third Party 
		BkavResponse bkavResponse = new BkavResponse();
		bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
	
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, companyKeyEntity.getPartnerToken());
		System.out.println(result);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		
		int status = (int) jsonObject.get("Status");
		boolean isOk = jsonObject.getBoolean("isOk");
		boolean isError = jsonObject.getBoolean("isError");
		
		String strObject = jsonObject.get("Object").toString();
		if (status == 0 && isOk == true && isError == false) {
			JSONArray jsonArray = new JSONArray(strObject);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject successObject = jsonArray.getJSONObject(i);

				ObjectMapper mapper = new ObjectMapper();
				String requestBody = mapper.writeValueAsString(commandDataEntity);
				int invoiceStatus = successObject.getInt("Status");
				eInvoiceService.updateEInvoice(id, invoiceStatus, successObject.getString("InvoiceGUID"), successObject.getString("MTC"), requestBody, strObject);
				if (invoiceStatus == 0) {
					eInvoiceService.updateEInvoiceSystemStatus(id, BkavConfigurationConstant.SYSTEM_STATUS_SUCCESS);
				} else {
					eInvoiceService.updateEInvoiceSystemStatus(id, BkavConfigurationConstant.SYSTEM_STATUS_FAILED);
				}
				
				invoiceCode = successObject.getString("MTC");
			}
		} 
		if (status == 1) {
			eInvoiceService.updateEInvoiceSystemStatus(id, BkavConfigurationConstant.SYSTEM_STATUS_FAILED);
		}
		
		return invoiceCode;
	}
	
	@Override
	public int getInvoiceStatus(EInvoiceEntity eInvoiceEntity) throws Exception {
		int invoiceStatus = 1;
		// Prepare Command Data
		CommandDataEntity commandDataEntity = prepareDataForGettingInvoiceDetail(eInvoiceEntity.getPartnerInvoiceStringId());
	 	CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(eInvoiceEntity.getProviderId());
	 	
	 	String partnerToken = companyKeyEntity.getPartnerToken();
		String encryptedCommandData = encryptionService.doEncryptedCommandData(commandDataEntity, partnerToken);
		
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		BkavResponse bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, partnerToken);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		int status = (int) jsonObject.get("Status");
		boolean isOk = jsonObject.getBoolean("isOk");
		boolean isError = jsonObject.getBoolean("isError");
		
		String strObject = jsonObject.get("Object").toString();
		if (status == 0 && isOk == true && isError == false) {
			JSONArray jsonArray = new JSONArray(strObject);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject successObject = jsonArray.getJSONObject(i);
				invoiceStatus = successObject.getInt("Status");
			}
		} 
		
		return invoiceStatus;
	}
	
	@Override
	public boolean cancelInvoice(String invoiceGUID) throws Exception {
		boolean isCanceled = false;
		// Prepare Command Data
		CommandDataEntity commandDataEntity = prepareDataForCancelingInvoice(invoiceGUID);
		
		// Get Token and Partner GUID by company
	 	EInvoiceEntity eInvoiceEntity = eInvoiceService.getByInvoiceGUID(invoiceGUID);
	 	if (eInvoiceEntity == null) {
	 		return false;
	 	}
			 	
	 	int companyId = eInvoiceEntity.getProviderId();
	 	CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(companyId);
	 	if (companyKeyEntity == null) {
	 		return false;
	 	}
				
	 	String partnerToken = companyKeyEntity.getPartnerToken();
		
		// Encrypted Command Data
		String encryptedCommandData = encryptionService.doEncryptedCommandData(commandDataEntity, partnerToken);
		
		// Call BKAV Third Party 
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		BkavResponse bkavResponse = new BkavResponse();
		bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
		
		// Receive Response and Decrypted Response Data
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, partnerToken);
		System.out.println(result);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		
		int status = (int) jsonObject.get("Status");
		boolean isOk = jsonObject.getBoolean("isOk");
		boolean isError = jsonObject.getBoolean("isError");
		if (status == 0 && isOk == true && isError == false) {
			eInvoiceService.updateEInvoiceStatus(eInvoiceEntity.getId(), BkavConfigurationConstant.INVOICE_STATUS_CANCELLED);
			isCanceled = true;
		}
		
		String strObject = jsonObject.get("Object").toString();
		System.out.println(strObject);
		
		return isCanceled;
	}
	
	
	@Override
	public BkavResult getInvoiceDetail(String invoiceGUID) throws Exception {
		BkavResult bkavResult = new BkavResult();
		
		// Prepare Command Data
		CommandDataEntity commandDataEntity = prepareDataForGettingInvoiceDetail(invoiceGUID);
		
		// Get Token and Partner GUID by company
	 	EInvoiceEntity eInvoiceEntity = eInvoiceService.getByInvoiceGUID(invoiceGUID);
	 	if (eInvoiceEntity == null) {
	 		bkavResult.setIsError(false);
			bkavResult.setStatus(0);
			bkavResult.setIsOk(false);
			bkavResult.setResult("Invoice GUID không tồn tại");
			return bkavResult;
	 	}
	 	
	 	int companyId = eInvoiceEntity.getProviderId();
	 	CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(companyId);
	 	if (companyKeyEntity == null) {
	 		bkavResult.setIsError(false);
			bkavResult.setStatus(0);
			bkavResult.setIsOk(false);
			bkavResult.setResult("Công ty không tồn tại");
			return bkavResult;
	 	}
		
	 	String partnerToken = companyKeyEntity.getPartnerToken();
		
		// Encrypted Command Data
		String encryptedCommandData = encryptionService.doEncryptedCommandData(commandDataEntity, partnerToken);
		
		// Call BKAV Third Party 
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		BkavResponse bkavResponse = new BkavResponse();
		bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
		
		// Receive Response and Decrypted Response Data
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, partnerToken);
		System.out.println(result);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		
		int status = (int) jsonObject.get("Status");
		boolean isOk = jsonObject.getBoolean("isOk");
		boolean isError = jsonObject.getBoolean("isError");
		
		bkavResult.setIsError(isError);
		bkavResult.setStatus(status);
		bkavResult.setIsOk(isOk);
		
		String strObject = jsonObject.get("Object").toString();
		System.out.println(strObject);
		
		if (status == 0 && isOk == true && isError == false) {
			// Parse Invoice Detail with Object Invoice Detail
			JSONObject jsonResult = new JSONObject(strObject);
			
			String strInvoice = jsonResult.getString("Invoice");
			String strListInvoiceDetailsWS = jsonResult.getString("ListInvoiceDetailsWS");
			String strListInvoiceAttachFileWS = jsonResult.getString("ListInvoiceAttachFileWS");
			
			// Parse Invoice Object
			InvoiceDetail invoiceDetail = parseInvoiceDetailFromJson(strInvoice);
			// Parse Invoice Detail WS
			List<InvoiceDetailsWSResult> listInvoiceDetailsWSResult = parseListInvoiceDetailsWSFromJson(strListInvoiceDetailsWS);
			// Parse Invoice Attach File WS
			List<InvoiceAttachFileWS> listInvoiceAttachFileWS = parseListInvoiceAttachFileWSFromJson(strListInvoiceAttachFileWS);
			
			
			InvoiceDetailResult invoiceDetailResult = new InvoiceDetailResult();
			invoiceDetailResult.setInvoiceDetail(invoiceDetail);
			invoiceDetailResult.setListInvoiceDetailsWSResult(listInvoiceDetailsWSResult);
			invoiceDetailResult.setListInvoiceAttachFileWS(listInvoiceAttachFileWS);
			
			invoiceDetailResult.setPartnerInvoiceID(jsonResult.getInt("PartnerInvoiceID"));
			invoiceDetailResult.setPartnerInvoiceStringID(jsonResult.getString("PartnerInvoiceStringID"));
			invoiceDetailResult.setIAAutoSign(jsonResult.getBoolean("IsIAAutoSign"));
			invoiceDetailResult.setIADontAutoSign(jsonResult.getBoolean("IsIADontAutoSign"));
			invoiceDetailResult.setInvoiceAction(jsonResult.getInt("InvoiceAction"));
			invoiceDetailResult.setIsSetInvoiceNo(jsonResult.getBoolean("IsSetInvoiceNo"));
			invoiceDetailResult.setTransactionID(jsonResult.getInt("TransactionID"));
			invoiceDetailResult.setIsIA_DontSendSMS_DontSendEmail(jsonResult.getBoolean("IsIA_DontSendSMS_DontSendEmail"));
			invoiceDetailResult.setIsIA_DontSendSMS_SendEmail(jsonResult.getBoolean("IsIA_DontSendSMS_SendEmail"));
			invoiceDetailResult.setIsIA_SendSMS_SendEmail(jsonResult.getBoolean("IsIA_SendSMS_SendEmail"));
			invoiceDetailResult.setIsIADontSendEmail(jsonResult.getBoolean("IsIADontSendEmail"));
			invoiceDetailResult.setIsIADontSendSMS(jsonResult.getBoolean("IsIADontSendSMS"));
			invoiceDetailResult.setIsIASendEmail(jsonResult.getBoolean("IsIASendEmail"));
			invoiceDetailResult.setIsIASendSMS(jsonResult.getBoolean("IsIASendSMS"));
			invoiceDetailResult.setTransactionStringID(jsonResult.getString("TransactionStringID"));
			
			bkavResult.setResult(invoiceDetailResult);
			
		} else {
			// Error, Parse with Error Object
			bkavResult.setResult(null);
		}
		
		return bkavResult;
	}

	@Override
	public BkavResult getInvoiceStatus(String invoiceGUID) throws Exception {
		BkavResult bkavResult = new BkavResult();
		// Prepare Command Data
		CommandDataEntity commandDataEntity = prepareDataForGettingInvoiceStatus(invoiceGUID);
		
		// Get Token and Partner GUID by company
	 	EInvoiceEntity eInvoiceEntity = eInvoiceService.getByInvoiceGUID(invoiceGUID);
	 	if (eInvoiceEntity == null) {
	 		bkavResult.setResult(null);
	 		bkavResult.setStatus(1);
	 		return null;
	 	}
	 	int companyId = eInvoiceEntity.getProviderId();
	 	CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(companyId);
	 	String partnerToken = companyKeyEntity.getPartnerToken();
	 	
		// Encrypted Command Data
		String encryptedCommandData = encryptionService.doEncryptedCommandData(commandDataEntity, partnerToken);
		
		// Call BKAV Third Party 
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		BkavResponse bkavResponse = new BkavResponse();
		bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
		
		// Receive Response and Decrypted Response Data
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, partnerToken);
		System.out.println(result);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		
		
		bkavResult.setIsError(jsonObject.getBoolean("isError"));
		bkavResult.setStatus(jsonObject.getInt("Status"));
		bkavResult.setIsOk(jsonObject.getBoolean("isOk"));
		bkavResult.setResult(BkavInvoiceUtil.parseInvoiceStatus(jsonObject.getInt("Object")));
		
		return bkavResult;
	}

	@Override
	public BkavResult getInvoiceHistory(String invoiceGUID) throws Exception {
		// Prepare Command Data
		CommandDataEntity commandDataEntity = prepareDataForGettingInvoiceHistory(invoiceGUID);
		
		// Get Token and Partner GUID by company
	 	EInvoiceEntity eInvoiceEntity = eInvoiceService.getByInvoiceGUID(invoiceGUID);
	 	int companyId = eInvoiceEntity.getProviderId();
	 	CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(companyId);	
	 	String partnerToken = companyKeyEntity.getPartnerToken();
	 	
		// Encrypted Command Data
		String encryptedCommandData = encryptionService.doEncryptedCommandData(commandDataEntity, partnerToken);
		
		// Call BKAV Third Party 
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		BkavResponse bkavResponse = new BkavResponse();
		bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
		
		// Receive Response and Decrypted Response Data
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, partnerToken);
		System.out.println(result);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		
		int status = (int) jsonObject.get("Status");
		boolean isOk = jsonObject.getBoolean("isOk");
		boolean isError = jsonObject.getBoolean("isError");
		
		BkavResult bkavResult = new BkavResult();
		bkavResult.setIsError(isError);
		bkavResult.setStatus(status);
		bkavResult.setIsOk(isOk);
		
		String strObject = jsonObject.get("Object").toString();
		System.out.println(strObject);
		
		if (status == 0 && isOk == true && isError == false) {
			// Parse Invoice History with Object Invoice History
			List<InvoiceHistory> listInvoiceHistory = new ArrayList<InvoiceHistory>();
			JSONArray jsonArray = new JSONArray(strObject);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				
				InvoiceHistory invoiceHistory = new InvoiceHistory();
				invoiceHistory.setId(obj.getInt("ID"));
				invoiceHistory.setFuncId(obj.getInt("FuncId"));
				invoiceHistory.setCreatedDate(obj.getString("CreateDate"));
				invoiceHistory.setIp(obj.getString("IP"));
				invoiceHistory.setLogContent(obj.getString("LogContent"));
				invoiceHistory.setObjectGUID(obj.getString("ObjectGUID"));
				invoiceHistory.setStt(obj.getInt("STT"));
				invoiceHistory.setUserId(obj.getInt("UserID"));
				invoiceHistory.setUserName(obj.getString("UserName"));
				
					
				listInvoiceHistory.add(invoiceHistory);
			}
			bkavResult.setResult(listInvoiceHistory);
		} else {
			bkavResult.setResult(null);
		}
		
		return bkavResult;
	}
	
	@Override
	public CompanyInforDto getCompanyInformationByTaxCode(String taxCode, long provideId) throws Exception {
		CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(provideId);	
	 	String partnerToken = companyKeyEntity.getPartnerToken();
		// Prepare Command Data
		CommandDataEntity commandDataEntity = prepareDataForGettingCompanyInformationByTaxCode(taxCode);
		
		// Encrypted Command Data
		String encryptedCommandData = encryptionService.doEncryptedCommandData(commandDataEntity, partnerToken);
		
		// Call BKAV Third Party 
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		BkavResponse bkavResponse = new BkavResponse();
		bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
		
		// Receive Response and Decrypted Response Data
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, partnerToken);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		int status = (int) jsonObject.get("Status");
		boolean isOk = jsonObject.getBoolean("isOk");
		boolean isError = jsonObject.getBoolean("isError");
		
		String strObject = jsonObject.get("Object").toString();
		if (status == 0 && isOk == true && isError == false) {
			// Parse result to Company Object
			JSONObject jsonCompany = new JSONObject(strObject);
			
			CompanyInforDto companyInforDto = new CompanyInforDto();
			companyInforDto.setAlternativeAddress(jsonCompany.getString("DiaChiGiaoDichPhu"));
			companyInforDto.setCompanyName(jsonCompany.getString("TenChinhThuc"));
			companyInforDto.setOperationStatus(jsonCompany.getString("TrangThaiHoatDong"));
			companyInforDto.setPrimaryAddress(jsonCompany.getString("DiaChiGiaoDichChinh"));
			companyInforDto.setTaxCode(jsonCompany.getString("MaSoThue"));
			
			return companyInforDto;
		}
		
		return null;
	}
	
	public static List<InvoiceDetailsWS> prepareListInvoiceDetailsWS(BkavTicketDto bkavTicketDto) {
		List<InvoiceDetailsWS> listInvoiceDetailsWS = new ArrayList<InvoiceDetailsWS>();
		
		List<PaymentConfig> paymentConfigs = bkavTicketDto.getPaymentConfiguration();
		
		for (PaymentConfig item : paymentConfigs) {
			InvoiceDetailsWS invoiceDetailsWS = new InvoiceDetailsWS();
			int price = BkavInvoiceUtil.calculatePriceBeforeTax(item.getPrice());
			int amount = BkavInvoiceUtil.calculateAmount(price, 1);
			invoiceDetailsWS.setQty(1);
			invoiceDetailsWS.setAmount(amount);
			invoiceDetailsWS.setPrice(price);
			invoiceDetailsWS.setIsDiscount(false);
			invoiceDetailsWS.setIsIncrease(null);
			invoiceDetailsWS.setItemName(item.getItemName());
			if (bkavTicketDto.getIsMonthly() == 1) {
				invoiceDetailsWS.setUnitName(BkavConfigurationConstant.INVOICE_MONTHLY_UNIT_NAME);
			} else {
				invoiceDetailsWS.setUnitName(BkavConfigurationConstant.INVOICE_DAILY_UNIT_NAME);
			}
			
			invoiceDetailsWS.setTaxRateID(BkavTaxRateConstant.TAX_BY_10_PERCENTAGE);
			invoiceDetailsWS.setTaxAmount(BkavInvoiceUtil.calculareTaxAmount(0.1, amount));
			listInvoiceDetailsWS.add(invoiceDetailsWS);
		}
		
		return listInvoiceDetailsWS;
	}
	
	public static List<InvoiceAttachFileWS> prepareInvoiceAttachFileWS() {
		List<InvoiceAttachFileWS> listInvoiceAttachFileWS = new ArrayList<InvoiceAttachFileWS>();
		
		return listInvoiceAttachFileWS;
	}

	public CommandDataEntity prepareDataForGettingInvoiceDetail(String invoiceGUID) {
		CommandDataEntity commandDataEntity = new CommandDataEntity();
		commandDataEntity.setCmdType(InvoiceCmdType.GET_INVOICE_DETAIL_INFOR);
		commandDataEntity.setCommandObject(invoiceGUID);
		
		return commandDataEntity;
	}

	public CommandDataEntity prepareDataForGettingInvoiceStatus(String invoiceGUID) {
		CommandDataEntity commandDataEntity = new CommandDataEntity();
		commandDataEntity.setCmdType(InvoiceCmdType.GET_INVOICE_STATUS);
		commandDataEntity.setCommandObject(invoiceGUID);
		
		return commandDataEntity;
	}
	
	public CommandDataEntity prepareDataForGettingInvoiceHistory(String invoiceGUID) {
		CommandDataEntity commandDataEntity = new CommandDataEntity();
		commandDataEntity.setCmdType(InvoiceCmdType.GET_INVOICE_HISTORY);
		commandDataEntity.setCommandObject(invoiceGUID);
		
		return commandDataEntity;
	}
	
	public CommandDataEntity prepareDataForGettingCompanyInformationByTaxCode(String taxCode) {
		CommandDataEntity commandDataEntity = new CommandDataEntity();
		commandDataEntity.setCmdType(InvoiceCmdType.GET_COMPANY_INFORMATION_BY_TAX_CODE);
		commandDataEntity.setCommandObject(taxCode);
		
		return commandDataEntity;
	}
	
	@Override
	public CommandDataEntity prepareDataForCancelingInvoice(String invoiceGUID) throws JsonProcessingException {
		List<CancelInvoice> listCommandObject = new ArrayList<CancelInvoice>();
		CancelInvoiceDto invoice = new CancelInvoiceDto();
		invoice.setInvoiceGuid(invoiceGUID);
		
//		EInvoiceEntity eInvoiceEntity = eInvoiceService.getByInvoiceGUID(invoiceGUID);
		
		CancelInvoice cancelInvoice = new CancelInvoice();
		cancelInvoice.setInvoice(invoice);
		cancelInvoice.setPartnerInvoiceID(0);
		cancelInvoice.setPartnerInvoiceStringID("");
		cancelInvoice.setListInvoiceAttachFileWS(prepareInvoiceAttachFileWS());
		cancelInvoice.setListInvoiceDetailsWS(new ArrayList<InvoiceDetailsWS>());
		listCommandObject.add(cancelInvoice);
		
		ObjectMapper mapper = new ObjectMapper();
		String strCommandData = mapper.writeValueAsString(listCommandObject);
		
		CommandDataEntity commandDataEntity = new CommandDataEntity();
		commandDataEntity.setCmdType(InvoiceCmdType.CANCEL_INVOICE_BY_INVOICE_GUID);
		commandDataEntity.setCommandObject(strCommandData);
		
		return commandDataEntity;
	}

	public InvoiceDetail parseInvoiceDetailFromJson(String strInvoice) throws JSONException {
		JSONObject jsonObject = new JSONObject(strInvoice);
		
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		
		invoiceDetail.setBillCode(jsonObject.getString("BillCode"));
		invoiceDetail.setBuyerAddress(jsonObject.getString("BuyerAddress"));
		invoiceDetail.setBuyerBankAccount(jsonObject.getString("BuyerBankAccount"));
		invoiceDetail.setBuyerName(jsonObject.getString("BuyerName"));
		invoiceDetail.setBuyerTaxCode(jsonObject.getString("BuyerTaxCode"));
		invoiceDetail.setBuyerUnitName(jsonObject.getString("BuyerUnitName"));
		invoiceDetail.setCurrencyID(jsonObject.getString("CurrencyID"));
		invoiceDetail.setExchangeRate(jsonObject.getInt("ExchangeRate"));
		invoiceDetail.setInvoiceCode(jsonObject.getString("InvoiceCode"));
		invoiceDetail.setInvoiceDate(jsonObject.getString("InvoiceDate"));
		invoiceDetail.setInvoiceForm(jsonObject.getString("InvoiceForm"));
		invoiceDetail.setInvoiceGUID(jsonObject.getString("InvoiceGUID"));
		invoiceDetail.setInvoiceNo(jsonObject.getInt("InvoiceNo"));
		invoiceDetail.setInvoiceSerial(jsonObject.getString("InvoiceSerial"));
		invoiceDetail.setInvoiceStatusID(jsonObject.getInt("InvoiceStatusID"));
		invoiceDetail.setInvoiceTypeID(jsonObject.getInt("InvoiceTypeID"));
		invoiceDetail.setNote(jsonObject.getString("Note"));
		invoiceDetail.setOriginalInvoiceIdentify(jsonObject.getString("OriginalInvoiceIdentify"));
		invoiceDetail.setPayMethodID(jsonObject.getInt("PayMethodID"));
		invoiceDetail.setReceiverAddress(jsonObject.getString("ReceiverAddress"));
		invoiceDetail.setReceiverEmail(jsonObject.getString("ReceiverEmail"));
		invoiceDetail.setReceiverMobile(jsonObject.getString("ReceiverMobile"));
		invoiceDetail.setReceiverName(jsonObject.getString("ReceiverName"));
		invoiceDetail.setReceiveTypeID(jsonObject.getInt("ReceiveTypeID"));
		invoiceDetail.setSignedDate(jsonObject.getString("SignedDate"));
		invoiceDetail.setUiDefine(jsonObject.getString("UIDefine"));
		invoiceDetail.setUserDefine(jsonObject.getString("UserDefine"));
		invoiceDetail.setSellerTaxCode(jsonObject.getString("SellerTaxCode"));
		
		return invoiceDetail;
	}

	@Override
	public List<InvoiceDetailsWSResult> parseListInvoiceDetailsWSFromJson(String strListInvoiceDetailsWS) throws JSONException {
		List<InvoiceDetailsWSResult> listInvoiceDetailsWSResult = new ArrayList<InvoiceDetailsWSResult>();
		
		JSONArray jsonArray = new JSONArray(strListInvoiceDetailsWS);
		
		for (int i = 0; i < jsonArray.length(); i++) {
			InvoiceDetailsWSResult invoiceDetailsWSResult = new InvoiceDetailsWSResult();
			JSONObject invoiceDetailsWSObject = jsonArray.getJSONObject(i);
			
			invoiceDetailsWSResult.setAmount(invoiceDetailsWSObject.getDouble("Amount"));
			if (!invoiceDetailsWSObject.getString("IsDiscount").equals("null")) {
				invoiceDetailsWSResult.setIsDiscount(invoiceDetailsWSObject.getBoolean("IsDiscount"));
			}
			if (!invoiceDetailsWSObject.getString("IsIncrease").equals("null")) {
				invoiceDetailsWSResult.setIsIncrease(invoiceDetailsWSObject.getBoolean("IsIncrease"));
			}
			invoiceDetailsWSResult.setItemName(invoiceDetailsWSObject.getString("ItemName"));
			invoiceDetailsWSResult.setPrice(invoiceDetailsWSObject.getDouble("Price"));
			invoiceDetailsWSResult.setQty(invoiceDetailsWSObject.getInt("Qty"));
			invoiceDetailsWSResult.setTaxAmount(invoiceDetailsWSObject.getDouble("TaxAmount"));
			invoiceDetailsWSResult.setTaxRateID(invoiceDetailsWSObject.getInt("TaxRateID"));
			invoiceDetailsWSResult.setTaxRate(invoiceDetailsWSObject.getDouble("TaxRate"));
			invoiceDetailsWSResult.setUnitName(invoiceDetailsWSObject.getString("UnitName"));
			invoiceDetailsWSResult.setUserDefineDetails(invoiceDetailsWSObject.getString("UserDefineDetails"));	
			invoiceDetailsWSResult.setDiscountRate(invoiceDetailsWSObject.getDouble("DiscountRate"));
			invoiceDetailsWSResult.setDiscountAmount(invoiceDetailsWSObject.getDouble("DiscountAmount"));
			invoiceDetailsWSResult.setItemCode(invoiceDetailsWSObject.getString("ItemCode"));
			invoiceDetailsWSResult.setItemTypeID(invoiceDetailsWSObject.getInt("ItemTypeID"));
			
			listInvoiceDetailsWSResult.add(invoiceDetailsWSResult);
		}
		
		return listInvoiceDetailsWSResult;
	}

	@Override
	public List<InvoiceAttachFileWS> parseListInvoiceAttachFileWSFromJson(String strListInvoiceAttachFileWS) throws JSONException {
		List<InvoiceAttachFileWS> listInvoiceAttachFileWS = new ArrayList<InvoiceAttachFileWS>();
		
		JSONArray jsonArray = new JSONArray(strListInvoiceAttachFileWS);
		
		for (int i = 0; i < jsonArray.length(); i++) {
			InvoiceAttachFileWS invoiceAttachFileWS = new InvoiceAttachFileWS();
			JSONObject invoiceDetailsWSObject = jsonArray.getJSONObject(i);
			
			invoiceAttachFileWS.setFileContent(invoiceDetailsWSObject.getString("FileContent"));
			invoiceAttachFileWS.setFileExtension(invoiceDetailsWSObject.getString("FileExtension"));
			invoiceAttachFileWS.setFileName(invoiceDetailsWSObject.getString("FileName"));
			
			listInvoiceAttachFileWS.add(invoiceAttachFileWS);
		}
		
		return listInvoiceAttachFileWS;
	}

	@Override
	public CommandDataEntity prepareDataForCreatingInvoice(BkavTicketDto bkavTicketDto) throws JsonProcessingException {
		List<CommandObject> listCommandObject = new ArrayList<CommandObject>();

		CommandObject commandObject = new CommandObject();
		commandObject.setInvoice(prepareInvoiceData(bkavTicketDto));
		commandObject.setPartnerInvoiceID(0);
		commandObject.setPartnerInvoiceStringID(bkavTicketDto.getPartnerInvoiceStringId());
		commandObject.setListInvoiceAttachFileWS(prepareInvoiceAttachFileWS());
		commandObject.setListInvoiceDetailsWS(prepareListInvoiceDetailsWS(bkavTicketDto));
		
		listCommandObject.add(commandObject);

		ObjectMapper mapper = new ObjectMapper();
		String strCommandData = mapper.writeValueAsString(listCommandObject);
		
		CommandDataEntity commandDataEntity = new CommandDataEntity();
		commandDataEntity.setCmdType(InvoiceCmdType.CREATE_INVOICE_WITH_FORM);
		commandDataEntity.setCommandObject(strCommandData);

		return commandDataEntity;
	}

	public Invoice prepareInvoiceData(BkavTicketDto bkavTicketDto) {
		Invoice invoice = new Invoice();
		
		invoice.setBillCode(BkavConfigurationConstant.INVOCIE_BILL_CODE);
		invoice.setBuyerAddress(bkavTicketDto.getBuyerAddress());
		invoice.setBuyerBankAccount(bkavTicketDto.getBuyerBankAccount());
		invoice.setBuyerName(bkavTicketDto.getBuyerName());
		invoice.setBuyerTaxCode(bkavTicketDto.getBuyerTaxCode());
		invoice.setBuyerUnitName(bkavTicketDto.getBuyerUnitName());
		invoice.setCurrencyID(BkavConfigurationConstant.CURRENCY_ID_VND);
		invoice.setExchangeRate(BkavConfigurationConstant.EXCHANGE_RATE_VND);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(BkavConfigurationConstant.INVOICE_DATE_FORMAT);
		String str = dateFormat.format(new Date());
		
		invoice.setInvoiceDate(str);
		invoice.setInvoiceForm(BkavConfigurationConstant.INVOICE_FORM);
		invoice.setInvoiceNo(0);
		
		invoice.setInvoiceSerial(BkavConfigurationConstant.INVOICE_SERIAL);
		invoice.setInvoiceTypeID(BkavConfigurationConstant.INVOICE_TYPE_ID);
		invoice.setNote(BkavConfigurationConstant.INVOICE_NOTE);
		invoice.setOriginalInvoiceIdentify(BkavConfigurationConstant.ORIGINAL_INVOICE_IDENTIFY);
		invoice.setPayMethodID(ReceiveInvoiceTypeConstant.PAY_BY_CREDIT_TRANSFER);
		invoice.setReceiverAddress(bkavTicketDto.getReceiverAddress());
		invoice.setReceiverEmail(bkavTicketDto.getReceiverEmail());
		invoice.setReceiverMobile(BkavInvoiceUtil.convertPhoneNumber(bkavTicketDto.getReceiverMobile()));
		invoice.setReceiverName(bkavTicketDto.getReceiverName());
		if (!bkavTicketDto.getReceiverEmail().equals("") && !bkavTicketDto.getReceiverEmail().isEmpty()) {
			invoice.setReceiveTypeID(ReceiveInvoiceTypeConstant.RECEIVE_BY_EMAIL);
		} else {
			invoice.setReceiveTypeID(ReceiveInvoiceTypeConstant.RECEIVE_BY_SMS);
		}

		return invoice;
	}

//	@Override
//	public void handleFailedInvoice() throws Exception {
//		List<EInvoiceEntity> eInvoices = eInvoiceService.getInvoiceFailed();
//		
//		for (EInvoiceEntity eInvoice : eInvoices) {
//			boolean isReCreated = reCreateInvoice(eInvoice);
//			if (isReCreated == false) {
//				eInvoiceService.updateEInvoiceStatus(eInvoice.getId(), BkavConfigurationConstant.INVOICE_STATUS_RECREATED_FAILED);
//			}
//		}
//		
//	}
	
	@Override
	public boolean reCreateInvoice(EInvoiceEntity eInvoice) throws Exception {
		boolean isCreated = false;
		CompanyKeyEntity companyKeyEntity = companyKeyService.findByCompanyId(eInvoice.getProviderId());
		
		System.out.println(eInvoice.getRequestBody());
	 	
	 	JSONObject requestBody = new JSONObject(eInvoice.getRequestBody());
	 	System.out.println(requestBody.toString());
	 	String encryptedCommandData = encryptionService.doEncryptedRequestBody(requestBody.toString(), companyKeyEntity.getPartnerToken());
		
		BkavRequest bkavRequest = new BkavRequest();
		bkavRequest.setPartnerGUID(companyKeyEntity.getPartnerGuid());
		bkavRequest.setCommandData(encryptedCommandData);
		bkavRequest.setUrl(configurationCache.get("URL"));
		
		// Call BKAV Third Party 
		BkavResponse bkavResponse = new BkavResponse();
		bkavResponse = bkavExecCommand.doExecCommand(bkavRequest);
	
		String decryptedResult = bkavResponse.getDecryptedResult();
		String result = encryptionService.doDecryptedCommamdData(decryptedResult, companyKeyEntity.getPartnerToken());
		System.out.println(result);
		
		// Parse to Object
		JSONObject jsonObject = new JSONObject(result);
		
		int status = (int) jsonObject.get("Status");
		boolean isOk = jsonObject.getBoolean("isOk");
		boolean isError = jsonObject.getBoolean("isError");
		
		String strObject = jsonObject.get("Object").toString();
		if (status == 0 && isOk == true && isError == false) {
			JSONArray jsonArray = new JSONArray(strObject);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject successObject = jsonArray.getJSONObject(i);
				int invoiceStatus = successObject.getInt("Status");
				if (invoiceStatus == 0) {
					eInvoiceService.updateEInvoice(eInvoice.getId(), invoiceStatus, successObject.getString("InvoiceGUID"), successObject.getString("MTC"), eInvoice.getRequestBody(), strObject);
					isCreated = true;
				} else {
					isCreated = false;
				}
			}
		} 
		
		return isCreated;
	}
}
