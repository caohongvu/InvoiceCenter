package net.cis.repository.invoice.center;


import org.springframework.data.jpa.repository.JpaRepository;

import net.cis.jpa.entity.ConfigurationEntity;

/**
 * Created by NhanNguyen
 */
public interface ConfigurationRepository  extends JpaRepository<ConfigurationEntity, Long> {
	
	ConfigurationEntity findByName(String name);

}
