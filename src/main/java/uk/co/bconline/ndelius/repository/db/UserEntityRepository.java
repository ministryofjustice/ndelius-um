package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.UserEntity;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long>
{
    Optional<UserEntity> findFirstByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
    @Query("SELECT u.id FROM UserEntity u WHERE u.username = ?1")
	Optional<Long> getUserId(String username);
}
