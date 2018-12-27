package net.cis.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import net.cis.common.util.constant.BkavConfigurationConstant;
import net.cis.common.util.constant.InvoiceConstant;
import net.cis.dto.BkavTicketDto;
import net.cis.jpa.entity.EInvoiceEntity;
import net.cis.repository.invoice.center.EInvoiceRepository;
import net.cis.service.EInvoiceService;
import net.cis.service.EmailService;
import net.cis.service.InvoiceService;
/**
 * Created by NhanNguyen on 19/10/2018
 */
@Service
public class EInvoiceServiceImpl implements EInvoiceService {

	ModelMapper mapper;
	
	@Autowired
	InvoiceService invoiceService;
	
	@Autowired
	EInvoiceRepository eInvoiceRepository;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	EntityManager entityManager;
	
	@PostConstruct
	public void initialize() throws Exception {
		mapper = new ModelMapper();
	}

	@Scheduled(fixedDelay=5*60*1000)
	private void checkAndResendNotMatchInvoice() throws Exception {
		List<EInvoiceEntity> notMatchStatusEInvoices = eInvoiceRepository.findByInvoiceStatusAndSystemStatus(BkavConfigurationConstant.INVOICE_STATUS_FAILED, BkavConfigurationConstant.SYSTEM_STATUS_FAILED);
		
		for(EInvoiceEntity invoiceEntity : notMatchStatusEInvoices) {
			int status = invoiceService.getInvoiceStatus(invoiceEntity);
			if (status == 0) {
				//Nếu đã bên BKAV đã phát hành invoice này, thì chỉ việc update lại status của invoice này bên mình
				updateEInvoiceStatus(invoiceEntity.getId(), BkavConfigurationConstant.INVOICE_STATUS_SUCCESS);
			} else {
				// Nếu bên BKAV chưa phát hành thành công invoice này, 
				// thì gọi phát hành lại và update lại status của invoice này theo kết quả trả về
				boolean isReCreatedSuccessfully = invoiceService.reCreateInvoice(invoiceEntity);
				
				//Nếu đã gửi qua bên BKAV và nhận về kết quả không thành công, 
				// thì update lại status của invoice thành không thành công, và gửi email thông báo
				if (isReCreatedSuccessfully == false) {
					updateEInvoiceStatus(invoiceEntity.getId(), BkavConfigurationConstant.INVOICE_STATUS_RECREATED_FAILED);
					updateEInvoiceSystemStatus(invoiceEntity.getId(), BkavConfigurationConstant.SYSTEM_STATUS_RE_CREATE_FAILED);
					
					//Kiểm tra chỉ send email trong trường hợp không thể xuất hóa đơn thành công nhé, 
					// edit lại content để lấy đúng thông tin cần thiết
					StringBuilder emailContent = new StringBuilder();

					emailContent.append("Không thể phát hành hóa đơn điện tử cho hóa đơn:<strong> " + invoiceEntity.getInvoiceCode() + "</strong>\n");
					emailContent.append("Của khách hàng:<strong> " + invoiceEntity.getCustomerEmail() + " - " + invoiceEntity.getCustomerPhone() + " </strong>\n");
					emailContent.append("Ở điểm đỗ:<strong> " + invoiceEntity.getCppId() + " </strong>\n");
					emailContent.append("Của vé: <strong> " + invoiceEntity.getTicketId() + " </strong>\n");
					emailContent.append("Của giao dịch: <strong> " + invoiceEntity.getTransactionId() + " </strong>\n");
					
					emailService.send(InvoiceConstant.EMAIL_FAILURE_TITLE, emailContent.toString());
				}
			}
		}
	}
	
	@Override
	public long createEInvoice(BkavTicketDto bkavTicketDto) {
		EInvoiceEntity eInvoice = parseEInvoice(bkavTicketDto);
		try {
			eInvoiceRepository.save(eInvoice);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return eInvoice.getId();
	}
	
	public static EInvoiceEntity parseEInvoice(BkavTicketDto bkavTicketDto) {
		EInvoiceEntity eInvoice = new EInvoiceEntity();
		
		eInvoice.setCustomerEmail(bkavTicketDto.getReceiverEmail());
		eInvoice.setCustomerPhone(bkavTicketDto.getReceiverMobile());
		eInvoice.setInvoiceGUID("");
		eInvoice.setInvoiceStatus(0);
		eInvoice.setProviderId(bkavTicketDto.getProviderId());
		eInvoice.setTransactionAmount(bkavTicketDto.getTransactionAmount());
		eInvoice.setTicketId(bkavTicketDto.getTicketId());
		eInvoice.setCppId(bkavTicketDto.getCppId());
		eInvoice.setTransactionId(bkavTicketDto.getTransactionId());
		eInvoice.setRequestBody("");
		eInvoice.setResponseBody("");
		eInvoice.setPartnerInvoiceStringId(bkavTicketDto.getPartnerInvoiceStringId());
		eInvoice.setSystemStatus(BkavConfigurationConstant.SYSTEM_STATUS_PROCESSING);
		
		return eInvoice;
	}

	@Override
	public void updateEInvoice(long id, int invoiceStatus, String invoiceGUID, String invoiceCode, String requestBody, String responseBody) {
		EInvoiceEntity eInvoice = eInvoiceRepository.findById(id);
		eInvoice.setInvoiceStatus(invoiceStatus);
		eInvoice.setInvoiceGUID(invoiceGUID);
		eInvoice.setInvoiceCode(invoiceCode);
		eInvoice.setRequestBody(requestBody);
		eInvoice.setResponseBody(responseBody);
		
		eInvoiceRepository.save(eInvoice);
	}

	@Override
	public List<EInvoiceEntity> getByTicketId(String ticketId) {
		return eInvoiceRepository.findByTicketId(ticketId);
	}

	@Override
	public EInvoiceEntity getByInvoiceGUID(String invoiceGUID) {
		return eInvoiceRepository.findByInvoiceGUID(invoiceGUID);
	}

	@Override
	public EInvoiceEntity getByPartnerStringId(String partnerStringId) {
		return eInvoiceRepository.findByPartnerInvoiceStringId(partnerStringId);
	}

	@Override
	public void updateEInvoiceStatus(long id, int invoiceStatus) {
		EInvoiceEntity eInvoice = eInvoiceRepository.findById(id);
		eInvoice.setInvoiceStatus(invoiceStatus);
		
		eInvoiceRepository.save(eInvoice);	
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EInvoiceEntity> getInvoiceFailed(int providerId, String email, String phone) {
		String sql = "SELECT e FROM EInvoiceEntity e WHERE e.invoiceStatus = 2";
		
		if (providerId != 0) {
			sql += " AND e.providerId = " + providerId;
		}
		if (email != null && email != "" && !email.isEmpty()) {
			sql += " AND e.customerEmail LIKE %" + email + "% ";
		}
		if (phone != null && phone != "" && !phone.isEmpty()) {
			sql += " AND e.customerPhone LIKE '%%" + phone + "%%'";
		}
		
		Query query = entityManager.createQuery(sql);
		return query.getResultList();
	}

	@Override
	public void updateEInvoiceSystemStatus(long id, String systemStatus) {
		EInvoiceEntity eInvoice = eInvoiceRepository.findById(id);
		eInvoice.setSystemStatus(systemStatus);
		
		eInvoiceRepository.save(eInvoice);	
	}

	@Override
	public EInvoiceEntity getByTicketIdAndTranId(String ticketId, String transactionId) {
		return eInvoiceRepository.findDistinctByTicketIdAndTransactionId(ticketId, transactionId);
	}

}
