package uk.co.bconline.ndelius.repository.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.co.bconline.ndelius.model.entity.TeamEntity;

public interface TeamRepository extends JpaRepository<TeamEntity, Long>
{
	List<TeamEntity> findAllByEndDateIsNull();
	List<TeamEntity> findAllByEndDateIsNullAndProbationAreaCode(String probationAreaCode);
	Optional<TeamEntity> findByCode(String code);
}
