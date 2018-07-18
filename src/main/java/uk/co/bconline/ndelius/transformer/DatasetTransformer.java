package uk.co.bconline.ndelius.transformer;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;

@Component
public class DatasetTransformer {

	public Organisation map(OrganisationEntity organisationEntity) {
		return ofNullable(organisationEntity)
				.map(oe -> Organisation.builder()
						.code(oe.getCode())
						.description(oe.getDescription())
						.build())
				.orElse(null);
	}

	public List<Dataset> map(Collection<ProbationAreaEntity> entities) {
		return entities.stream()
				.map(this::map)
				.collect(toList());
	}

	public Dataset map(ProbationAreaEntity entity) {
		return Dataset.builder()
				.code(entity.getCode())
				.description(entity.getDescription())
				.active("Y".equalsIgnoreCase(entity.getSelectable()))
				.organisation(map(entity.getOrganisation()))
				.build();
	}
}
