package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;

import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<OrganisationEntity, Long> {
    Optional<OrganisationEntity> findByCode(String code);
}
