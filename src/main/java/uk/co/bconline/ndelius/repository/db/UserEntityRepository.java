package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entity.export.UserExportEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long>
{
    Optional<UserEntity> findFirstByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);

    @Query("SELECT u.id FROM UserEntity u WHERE UPPER(u.username) = UPPER(?1)")
	Optional<Long> getUserId(String username);

	@Query("SELECT u FROM UserExportEntity u " +
			"LEFT JOIN FETCH u.datasets " +
			"LEFT JOIN FETCH u.staff s " +
			"LEFT JOIN FETCH s.teams " +
			"LEFT JOIN FETCH s.grade " +
			"WHERE u.endDate is null OR u.endDate > current_date " +
			"ORDER BY u.username")
	Stream<UserExportEntity> export();
}
