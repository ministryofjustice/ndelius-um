package uk.co.bconline.ndelius.repository.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;

public interface ReferenceDataRepository extends JpaRepository<ReferenceDataEntity, Long> {
	List<ReferenceDataEntity> findAllBySelectableAndReferenceDataMasterCodeSetName(String selectable, String codeSetName);
	Optional<ReferenceDataEntity> findByCodeAndReferenceDataMasterCodeSetName(String code, String codeSetName);
}
