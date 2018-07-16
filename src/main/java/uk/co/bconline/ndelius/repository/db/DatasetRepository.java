package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;

import java.util.List;

public interface DatasetRepository extends JpaRepository<ProbationAreaEntity, Long> {
    public List<ProbationAreaEntity> findAllBySelectable(String selectable);
}
