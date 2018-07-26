package uk.co.bconline.ndelius.transformer;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;

@Component
public class OrganisationTransformer
{
	public List<Organisation> map(List<OrganisationEntity> organisations)
	{
		return organisations.stream()
				.map(organisation -> Organisation.builder()
						.code(organisation.getCode())
						.description(organisation.getDescription())
						.build())
				.collect(toList());
	}

}
