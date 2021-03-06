package uk.co.bconline.ndelius.transformer;

import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;
import uk.co.bconline.ndelius.model.entity.SubContractedProviderEntity;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Component
public class DatasetTransformer
{

	public Organisation map(OrganisationEntity organisationEntity)
	{
		return ofNullable(organisationEntity)
				.map(oe -> Organisation.builder()
						.code(oe.getCode())
						.description(oe.getDescription())
						.build())
				.orElse(null);
	}

	public OrganisationEntity map(Organisation organisation)
	{
		return ofNullable(organisation)
				.map(oe -> OrganisationEntity.builder()
						.code(oe.getCode())
						.description(oe.getDescription())
						.build())
				.orElse(null);
	}

	public List<Dataset> mapToDatasets(Collection<ProbationAreaEntity> entities)
	{
		return entities.stream()
				.filter(p -> !p.isEstablishment())
				.map(this::map)
				.sorted(Comparator.comparing(Dataset::getCode))
				.collect(toList());
	}

	public List<Dataset> mapToEstablishments(Collection<ProbationAreaEntity> entities)
	{
		return entities.stream()
				.filter(ProbationAreaEntity::isEstablishment)
				.map(this::map)
				.sorted(Comparator.comparing(Dataset::getCode))
				.collect(toList());
	}

	public Dataset map(ProbationAreaEntity entity)
	{
		return Dataset.builder()
				.code(entity.getCode())
				.description(entity.getDescription())
				.active(entity.isSelectable())
				.build();
	}

	public Dataset map(SubContractedProviderEntity entity)
	{
		return Dataset.builder()
				.code(entity.getCode())
				.description(entity.getDescription())
				.active(entity.getActive())
				.build();
	}
}
