package uk.co.bconline.ndelius.transformer;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;

@Component
public class ReferenceDataTransformer {

	public List<ReferenceData> map(Collection<ReferenceDataEntity> entities) {
		return entities.stream()
				.map(this::map)
				.collect(toList());
	}

	public ReferenceData map(ReferenceDataEntity item) {
		if (item == null) return null;
		return ReferenceData.builder()
				.code(item.getCode())
				.description(item.getDescription())
				.build();
	}

	public ReferenceDataEntity map(ReferenceData item) {
		if (item == null) return null;
		return ReferenceDataEntity.builder()
				.code(item.getCode())
				.description(item.getDescription())
				.build();
	}
}
