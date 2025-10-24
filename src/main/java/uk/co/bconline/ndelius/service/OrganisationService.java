package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.Organisation;

import java.util.List;
import java.util.Optional;

public interface OrganisationService {
    Optional<Long> getOrganisationId(String code);

    List<Organisation> getOrganisations();
}
