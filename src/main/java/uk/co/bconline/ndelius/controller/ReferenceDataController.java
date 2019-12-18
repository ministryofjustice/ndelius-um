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
import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.service.ReferenceDataService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReferenceDataController
{
	private final ReferenceDataService service;

	@Autowired
	public ReferenceDataController(ReferenceDataService service)
	{
		this.service = service;
	}

	@GetMapping("/staffgrades")
	@PreAuthorize("#oauth2.hasScope('UMBI008')")
	public ResponseEntity<List<ReferenceData>> getStaffGrades()
	{
		return new ResponseEntity<>(service.getStaffGrades(), HttpStatus.OK);
	}
}