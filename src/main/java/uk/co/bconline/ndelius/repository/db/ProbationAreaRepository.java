package uk.co.bconline.ndelius.repository.db;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;

import java.util.List;
import java.util.Optional;

public interface ProbationAreaRepository extends JpaRepository<ProbationAreaEntity, Long>
{
	@Query(value = "SELECT spgconfig.getNextStaffReference(probation_area_code_in => ?1) FROM DUAL", nativeQuery = true)
	String getNextStaffCode(String datasetCode);

	@Query("SELECT p FROM ProbationAreaEntity p WHERE p.selectable = true AND (p.establishment IS NULL OR p.establishment = false)")
	List<ProbationAreaEntity> findAllSelectableNonEstablishments();

	List<ProbationAreaEntity> findAllBySelectableTrueAndEstablishmentTrue();

	List<ProbationAreaEntity> findAllByUserLinks_User_Username(String username);

	Optional<ProbationAreaEntity> findByCode(String code);

	@Query("SELECT p.id FROM ProbationAreaEntity p WHERE p.code = ?1")
	Optional<Long> findIdByCode(String code);

	@Query("SELECT p.organisationId FROM ProbationAreaEntity p WHERE p.code = ?1")
	Optional<Long> findOrganisationIdByCode(String code);
}
