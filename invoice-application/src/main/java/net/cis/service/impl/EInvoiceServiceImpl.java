package net.cis.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.cis.dto.BkavTicketDto;
import net.cis.jpa.entity.EInvoiceEntity;
import net.cis.repository.invoice.center.EInvoiceRepository;
import net.cis.service.EInvoiceService;
/**
 * Created by NhanNguyen on 19/10/2018
 */
@Service
public class EInvoiceServiceImpl implements EInvoiceService {

	ModelMapper mapper;
	
	@Autowired
	EInvoiceRepository eInvoiceRepository;
	
	@PostConstruct
	public void initialize() {
		mapper = new ModelMapper();
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
		eInvoice.setProvideId(bkavTicketDto.getProviderId());
		eInvoice.setTransactionAmount(bkavTicketDto.getTransactionAmount());
		eInvoice.setTicketId(bkavTicketDto.getTicketId());
		eInvoice.setCppId(bkavTicketDto.getCppId());
		eInvoice.setTransactionId(bkavTicketDto.getTransactionId());
		
		return eInvoice;
	}

	@Override
	public void updateEInvoice(long id, int invoiceStatus, String invoiceGUID, String invoiceCode) {
		EInvoiceEntity eInvoice = eInvoiceRepository.findById(id);
		eInvoice.setInvoiceStatus(invoiceStatus);
		eInvoice.setInvoiceGUID(invoiceGUID);
		eInvoice.setInvoiceCode(invoiceCode);
		
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
	

}
