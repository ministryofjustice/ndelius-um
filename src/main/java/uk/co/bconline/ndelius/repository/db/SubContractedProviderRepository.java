package uk.co.bconline.ndelius.repository.db;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.SubContractedProviderEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubContractedProviderRepository extends JpaRepository<SubContractedProviderEntity, Long>
{
	@Cacheable("subContractedProvidersByProviderCode")
	List<SubContractedProviderEntity> findAllByProviderCodeAndActiveTrueAndEndDateBefore(String providerCode, LocalDate today);

	@Cacheable("subContractedProviderByCode")
	Optional<SubContractedProviderEntity> findByCode(String code);
}
