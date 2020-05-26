package uk.co.bconline.ndelius.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.ChangeNote;
import uk.co.bconline.ndelius.model.entity.ChangeNoteEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static uk.co.bconline.ndelius.util.NameUtils.combineNames;

@Slf4j
@Component
public class ChangeNoteTransformer {

	public List<ChangeNote> map(Collection<ChangeNoteEntity> entities) {
		return entities.stream()
				.map(this::map)
				.sorted(comparing(ChangeNote::getTime).reversed())
				.collect(toList());
	}

	public ChangeNote map(ChangeNoteEntity entity) {
		return map(entity.getUpdatedBy(), entity.getUpdatedAt(), entity.getNotes());
	}

	public ChangeNote map(UserEntity modifiedBy, LocalDateTime modifiedAt, String note) {
		if (modifiedAt == null || modifiedBy == null) return null;
		return ChangeNote.builder()
				.user(ChangeNote.User.builder()
						.forenames(combineNames(modifiedBy.getForename(), modifiedBy.getForename2()))
						.surname(modifiedBy.getSurname())
						.username(modifiedBy.getUsername())
						.build())
				.time(modifiedAt)
				.note(note)
				.build();
	}
}
