package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.SubContractedProviderEntity;

import java.util.List;
import java.util.Optional;

public interface SubContractedProviderRepository extends JpaRepository<SubContractedProviderEntity, Long>
{
	@Query("SELECT s FROM SubContractedProviderEntity s " +
			"WHERE s.provider.code = ?1 " +
			"AND s.active = true " +
			"AND (s.endDate IS NULL OR s.endDate >= SYSDATE)")
	List<SubContractedProviderEntity> findAllByProviderCode(String providerCode);

	Optional<SubContractedProviderEntity> findByCode(String code);
}
