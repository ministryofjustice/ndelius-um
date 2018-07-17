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

	public List<Dataset> map(Collection<ProbationAreaEntity> datasetEntity) {
		return datasetEntity.stream()
				.map(de -> Dataset.builder()
						.code(de.getCode())
						.description(de.getDescription())
						.active("Y".equalsIgnoreCase(de.getSelectable()))
						.organisation(map(de.getOrganisation()))
						.build())
						.collect(toList());
	}
}
