package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.Dataset;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DatasetService
{
	List<Dataset> getDatasets();
	List<Dataset> getDatasets(String username);
	Set<String> getDatasetCodes(String username);
	Optional<Long> getDatasetId(String code);
	Optional<Dataset> getDatasetByCode(String code);
	Optional<Long> getOrganisationIdByDatasetCode(String code);
	String getNextStaffCode(String datasetCode);
	List<Dataset> getSubContractedProviders(String datasetCode);
	Optional<Long> getSubContractedProviderId(String code);
	List<Dataset> getEstablishments();
}
