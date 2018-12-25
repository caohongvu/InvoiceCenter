package net.cis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.cis.jpa.entity.ConfigurationEntity;
import net.cis.repository.invoice.center.ConfigurationRepository;
import net.cis.service.ConfigurationService;


/**
 * Created by NhanNguyen
 */

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	
	@Autowired
	private ConfigurationRepository configurationRepository;

	@Override
	public ConfigurationEntity getWebService(String name) {
		return configurationRepository.findByName(name);
	}

}
 