package uk.co.bconline.ndelius.repository.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;

public interface DatasetRepository extends JpaRepository<ProbationAreaEntity, Long> {
	List<ProbationAreaEntity> findAllBySelectable(String selectable);
	List<ProbationAreaEntity> findAllByUsersWithDatasetUsername(String username);
	Optional<ProbationAreaEntity> findByCode(String code);
}
