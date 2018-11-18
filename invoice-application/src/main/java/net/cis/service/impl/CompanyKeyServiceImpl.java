package net.cis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.cis.jpa.entity.CompanyKeyEntity;
import net.cis.repository.invoice.center.CompanyKeyRepository;
import net.cis.service.CompanyKeyService;


/**
 * Created by NhanNguyen
 */

@Service
public class CompanyKeyServiceImpl implements CompanyKeyService {
	
	@Autowired
	private CompanyKeyRepository companyKeyRepository;

	@Override
	public CompanyKeyEntity findByCompanyId(long companyId) {
		return companyKeyRepository.findById(companyId);
	}

}
 