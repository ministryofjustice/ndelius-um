package uk.co.bconline.ndelius.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.bconline.ndelius.model.entity.ChangeNoteEntity;

import java.util.Set;

public interface ChangeNoteRepository extends JpaRepository<ChangeNoteEntity, Long> {
    Set<ChangeNoteEntity> getByUserId(long userId);

    boolean existsByUserId(long userId);
}
