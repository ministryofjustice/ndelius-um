package uk.co.bconline.ndelius.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.entity.OrganisationEntity;
import uk.co.bconline.ndelius.repository.db.OrganisationRepository;
import uk.co.bconline.ndelius.service.OrganisationService;

@Service
public class OrganisationServiceImpl implements OrganisationService
{
	private final OrganisationRepository repository;

	@Autowired
	public OrganisationServiceImpl(OrganisationRepository repository)
	{
		this.repository = repository;
	}

	@Override
	public Optional<Long> getOrganisationId(String code)
	{
		return repository.findByCode(code).map(OrganisationEntity::getId);
	}

}
