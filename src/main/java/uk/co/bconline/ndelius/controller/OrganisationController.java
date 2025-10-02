package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.service.OrganisationService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrganisationController
{
	private final OrganisationService organisationService;

	@Autowired
	public OrganisationController(OrganisationService organisationService)
	{
		this.organisationService = organisationService;
	}

	@GetMapping("/organisations")
	@PreAuthorize("hasAuthority('SCOPE_UMBI011')")
	public ResponseEntity<List<Organisation>> getOrganisations()
	{
		return new ResponseEntity<>(organisationService.getOrganisations(), HttpStatus.OK);
	}

}
