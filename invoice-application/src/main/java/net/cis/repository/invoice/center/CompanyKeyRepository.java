package net.cis.repository.invoice.center;


import org.springframework.data.jpa.repository.JpaRepository;
import net.cis.jpa.entity.CompanyKeyEntity;

/**
 * Created by NhanNguyen
 */
public interface CompanyKeyRepository  extends JpaRepository<CompanyKeyEntity, Long> {
	
	CompanyKeyEntity findById(long id);

}
