package uk.co.bconline.ndelius.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.Organisation;
import uk.co.bconline.ndelius.service.OrganisationService;

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

	@Interaction("UMBI011")
	@GetMapping("/organisations")
	public ResponseEntity<List<Organisation>> getOrganisations()
	{
		return new ResponseEntity<>(organisationService.getOrganisations(), HttpStatus.OK);
	}

}
