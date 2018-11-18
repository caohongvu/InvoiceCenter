package net.cis.service;

import net.cis.jpa.entity.CompanyKeyEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */
public interface CompanyKeyService {
    
    CompanyKeyEntity findByCompanyId(long companyId);
}
 