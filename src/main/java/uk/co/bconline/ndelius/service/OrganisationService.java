package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.Organisation;

public interface OrganisationService
{
	Optional<Long> getOrganisationId(String code);
	List<Organisation> getOrganisations();
}
