package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.Dataset;

public interface DatasetService
{
	List<Dataset> getDatasets();
	List<Dataset> getDatasets(String username);
	List<String> getDatasetCodes(String username);
	Optional<Long> getDatasetId(String code);
	Optional<Dataset> getDatasetByCode(String code);
}
