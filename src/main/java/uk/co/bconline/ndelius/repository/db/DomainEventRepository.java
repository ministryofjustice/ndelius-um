package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.DomainEventEntity;

public interface DomainEventRepository extends JpaRepository<DomainEventEntity, Long>
{
}
