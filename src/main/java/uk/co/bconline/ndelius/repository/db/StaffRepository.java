package uk.co.bconline.ndelius.repository.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.co.bconline.ndelius.model.entity.StaffEntity;

public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
	Optional<StaffEntity> findByCode(String code);

	Optional<StaffEntity> findByCodeAndEndDateIsNull(String code);
}
