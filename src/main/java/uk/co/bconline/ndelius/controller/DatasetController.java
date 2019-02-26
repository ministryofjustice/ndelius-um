package uk.co.bconline.ndelius.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.service.DatasetService;

import java.util.List;

import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api")
public class DatasetController
{
	private final DatasetService datasetService;

	@Autowired
	public DatasetController(DatasetService datasetService)
	{
		this.datasetService = datasetService;
	}

	@Interaction("UMBI006")
	@GetMapping(value = "/datasets", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Dataset>> getDatasets()
	{
		return ok(datasetService.getDatasets());
	}

	@Interaction("UMBI006")
	@GetMapping(value = "/dataset/{datasetCode}/nextStaffCode", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getNextStaffCode(@PathVariable("datasetCode") String datasetCode)
	{
		if (!datasetService.getDatasetByCode(datasetCode).isPresent()) return notFound().build();
		return ok(datasetService.getNextStaffCode(datasetCode));
	}

	@Interaction("UMBI006")
	@GetMapping(value = "/dataset/{datasetCode}/subContractedProviders", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Dataset>> getSubContractedProviders(@PathVariable("datasetCode") String datasetCode)
	{
		return ok(datasetService.getSubContractedProviders(datasetCode));
	}
}