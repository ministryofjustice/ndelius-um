package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ReferenceDataTransformer {

	public List<ReferenceData> map(Collection<ReferenceDataEntity> entities) {
		return entities.stream()
				.map(this::map)
				.collect(toList());
	}

	public ReferenceData map(ReferenceDataEntity entity) {
		return ReferenceData.builder()
				.code(entity.getCodeValue())
				.description(entity.getCodeDescription())
				.build();
	}
}
