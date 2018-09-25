package uk.co.bconline.ndelius.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.model.entity.OrganisationEntity;
import uk.co.bconline.ndelius.repository.db.OrganisationRepository;
import uk.co.bconline.ndelius.service.OrganisationService;
import uk.co.bconline.ndelius.transformer.OrganisationTransformer;

@Service
public class OrganisationServiceImpl implements OrganisationService
{
	private final OrganisationRepository repository;
	private final OrganisationTransformer transformer;

	@Autowired
	public OrganisationServiceImpl(OrganisationRepository repository, OrganisationTransformer transformer)
	{
		this.repository = repository;
		this.transformer = transformer;
	}

	@Override
	@Cacheable("organisationIds")
	public Optional<Long> getOrganisationId(String code)
	{
		return repository.findByCode(code).map(OrganisationEntity::getId);
	}

	@Override
	@Cacheable(value = "organisations", key = "'all'")
	public List<Organisation> getOrganisations()
	{
		List<OrganisationEntity> organisations = repository.findAll();
		return transformer.map(organisations);
	}
}
