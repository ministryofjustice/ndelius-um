package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;

import java.util.List;

public interface ReferenceDataRepository extends JpaRepository<ReferenceDataEntity, Long> {
	List<ReferenceDataEntity> findAllBySelectableAndReferenceDataMasterCodeSetName(String selectable, String codeSetName);
}
