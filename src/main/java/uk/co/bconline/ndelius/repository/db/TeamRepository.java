package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.TeamEntity;

import java.util.List;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
	List<TeamEntity> findAllByEndDateIsNull();
	List<TeamEntity> findAllByEndDateIsNullAndProbationAreaCode(String probationAreaCode);
}
