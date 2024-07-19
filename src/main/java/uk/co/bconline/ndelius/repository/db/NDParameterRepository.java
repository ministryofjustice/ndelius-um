package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.NDParameterEntity;

import java.util.Optional;

public interface NDParameterRepository extends JpaRepository<NDParameterEntity, Long>
{
    Optional<NDParameterEntity> findByNdParameter(String parameterName);
}
