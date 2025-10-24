package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.StaffTeamEntity;

import java.util.List;

public interface StaffTeamRepository extends JpaRepository<StaffTeamEntity, Long> {
    List<StaffTeamEntity> findStaffTeamEntitiesByStaffId(long staffId);
}
