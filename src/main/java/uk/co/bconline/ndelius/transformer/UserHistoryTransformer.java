package uk.co.bconline.ndelius.transformer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.UserHistoryItem;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.model.entity.UserHistoryEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static uk.co.bconline.ndelius.util.NameUtils.combineNames;

@Slf4j
@Component
public class UserHistoryTransformer {

	public List<UserHistoryItem> map(Collection<UserHistoryEntity> entities) {
		return entities.stream()
				.map(this::map)
				.sorted(comparing(UserHistoryItem::getTime).reversed())
				.collect(toList());
	}

	public UserHistoryItem map(UserHistoryEntity entity) {
		return map(entity.getUpdatedBy(), entity.getUpdatedAt(), entity.getNotes());
	}

	public UserHistoryItem map(UserEntity modifiedBy, LocalDateTime modifiedAt, String note) {
		if (modifiedAt == null || modifiedBy == null) return null;
		return UserHistoryItem.builder()
				.user(UserHistoryItem.User.builder()
						.forenames(combineNames(modifiedBy.getForename(), modifiedBy.getForename2()))
						.surname(modifiedBy.getSurname())
						.username(modifiedBy.getUsername())
						.build())
				.time(modifiedAt)
				.note(note)
				.build();
	}
}
