package net.cis.service;

import net.cis.jpa.entity.ConfigurationEntity;

/**
 * Created by NhanNguyen 19/10/2018
 */
public interface ConfigurationService {
    
	ConfigurationEntity getWebService(String name);
}
 