package uk.co.bconline.ndelius.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Optionals;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.ChangeNote;
import uk.co.bconline.ndelius.model.entity.ChangeNoteEntity;
import uk.co.bconline.ndelius.model.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static uk.co.bconline.ndelius.util.NameUtils.combineNames;

@Slf4j
@Component
public class ChangeNoteTransformer {

	public List<ChangeNote> map(Collection<ChangeNoteEntity> entities) {
		return entities.stream()
				.map(this::map)
				.flatMap(Optionals::toStream)
				.sorted(comparing(ChangeNote::getTime).reversed())
				.collect(toList());
	}

	public Optional<ChangeNote> map(ChangeNoteEntity entity) {
		return map(entity.getUpdatedBy(), entity.getUpdatedAt(), entity.getNotes());
	}

	public Optional<ChangeNoteEntity> mapToEntity(UserEntity user, UserEntity modifiedBy, LocalDateTime modifiedAt) {
		if (modifiedAt == null || modifiedBy == null) return Optional.empty();
		return Optional.of(ChangeNoteEntity.builder()
				.user(user)
				.updatedAt(modifiedAt)
				.updatedById(modifiedBy.getId())
				.build());
	}

	public Optional<ChangeNote> map(UserEntity modifiedBy, LocalDateTime modifiedAt, String note) {
		if (modifiedAt == null || modifiedBy == null) return Optional.empty();
		return Optional.of(ChangeNote.builder()
				.user(ChangeNote.User.builder()
						.forenames(combineNames(modifiedBy.getForename(), modifiedBy.getForename2()))
						.surname(modifiedBy.getSurname())
						.username(modifiedBy.getUsername())
						.build())
				.time(modifiedAt)
				.note(note)
				.build());
	}
}
