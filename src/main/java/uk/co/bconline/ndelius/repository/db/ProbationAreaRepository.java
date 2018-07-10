package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.entity.ProbationAreaEntity;

public interface ProbationAreaRepository extends JpaRepository<ProbationAreaEntity, Long> {
}
