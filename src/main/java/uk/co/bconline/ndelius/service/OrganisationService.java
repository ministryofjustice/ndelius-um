package uk.co.bconline.ndelius.service;

import java.util.Optional;

public interface OrganisationService
{
	Optional<Long> getOrganisationId(String code);
}
