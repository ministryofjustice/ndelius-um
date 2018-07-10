package uk.co.bconline.ndelius.repository.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.co.bconline.ndelius.entity.UserEntity;

public interface DBUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> getUserEntityByUsernameEqualsIgnoreCase(String username);
}
