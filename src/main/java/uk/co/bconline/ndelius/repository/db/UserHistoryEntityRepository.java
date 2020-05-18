package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.UserHistoryEntity;

import java.util.Set;

public interface UserHistoryEntityRepository extends JpaRepository<UserHistoryEntity, Long> {
    Set<UserHistoryEntity> getUserHistoryEntitiesByUserId(long userId);
}
