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

	@Cacheable("probationAreas")
	@Query("SELECT p FROM ProbationAreaEntity p WHERE p.selectable = true AND (p.establishment IS NULL OR p.establishment = false)")
	List<ProbationAreaEntity> findAllSelectableNonEstablishments();

	@Cacheable("establishments")
	List<ProbationAreaEntity> findAllBySelectableTrueAndEstablishmentTrue();

	List<ProbationAreaEntity> findAllByUserLinks_User_Username(String username);

	@Cacheable("probationAreasByCode")
	Optional<ProbationAreaEntity> findByCode(String code);

	@Cacheable("probationAreaIdsByCode")
	@Query("SELECT p.id FROM ProbationAreaEntity p WHERE p.code = ?1")
	Optional<Long> findIdByCode(String code);

	@Cacheable("organisationIdsByProbationAreaCode")
	@Query("SELECT p.organisationId FROM ProbationAreaEntity p WHERE p.code = ?1")
	Optional<Long> findOrganisationIdByCode(String code);
}
