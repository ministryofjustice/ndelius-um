package uk.co.bconline.ndelius.repository.db;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.SubContractedProviderEntity;

import java.util.List;
import java.util.Optional;

public interface SubContractedProviderRepository extends JpaRepository<SubContractedProviderEntity, Long>
{
	@Cacheable("subContractedProviderByProviderId")
	List<SubContractedProviderEntity> findAllByProviderCode(String providerCode);

	@Cacheable("subContractedProviderByCode")
	Optional<SubContractedProviderEntity> findByCode(String code);
}
