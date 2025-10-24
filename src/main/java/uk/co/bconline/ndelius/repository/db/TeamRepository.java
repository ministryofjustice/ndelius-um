package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.TeamEntity;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    List<TeamEntity> findAllByEndDateIsNull();

    List<TeamEntity> findAllByEndDateIsNullAndProbationAreaCode(String probationAreaCode);

    Optional<TeamEntity> findByCode(String code);

    @Query("SELECT t.id FROM TeamEntity t WHERE t.code = ?1 AND t.endDate IS NULL")
    Optional<Long> findIdByCode(String code);
}
