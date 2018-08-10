package uk.co.bconline.ndelius.repository.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.co.bconline.ndelius.model.entity.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> getUserEntityByUsernameEqualsIgnoreCase(String username);
    Optional<UserEntity> getUserEntityByStaffCodeEqualsIgnoreCase(String staffCode);
}
