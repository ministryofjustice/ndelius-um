package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.service.DatasetService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class DatasetController
{
	private final DatasetService datasetService;

	@Autowired
	public DatasetController(DatasetService datasetService)
	{
		this.datasetService = datasetService;
	}

	@Interaction("UMBI006")
	@GetMapping("/datasets")
	public ResponseEntity<List<Dataset>> getDatsets()
	{
		return new ResponseEntity<>(datasetService.getDatasets(), HttpStatus.OK);
	}
}