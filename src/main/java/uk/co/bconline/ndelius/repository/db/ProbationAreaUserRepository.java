package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.co.bconline.ndelius.model.entity.ProbationAreaUserEntity;

import java.util.List;

public interface ProbationAreaUserRepository extends JpaRepository<ProbationAreaUserEntity, Long> {
    List<ProbationAreaUserEntity> findProbationAreaUserEntitiesByUserUsername(String username);
}
