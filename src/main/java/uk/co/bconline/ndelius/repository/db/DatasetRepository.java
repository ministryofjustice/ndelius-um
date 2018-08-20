package uk.co.bconline.ndelius.repository.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;

public interface DatasetRepository extends JpaRepository<ProbationAreaEntity, Long>
{
	@Query(value = "SELECT spgconfig.getNextStaffReference(probation_area_code_in => ?1) FROM DUAL", nativeQuery = true)
	String getNextStaffCode(String datasetCode);
	List<ProbationAreaEntity> findAllBySelectable(String selectable);
	List<ProbationAreaEntity> findAllByUserLinks_User_Username(String username);
	Optional<ProbationAreaEntity> findByCode(String code);
}
