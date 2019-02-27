package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;
import uk.co.bconline.ndelius.model.entity.SubContractedProviderEntity;
import uk.co.bconline.ndelius.repository.db.DatasetRepository;
import uk.co.bconline.ndelius.repository.db.SubContractedProviderRepository;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.transformer.DatasetTransformer;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class DatasetServiceImpl implements DatasetService
{
	private final DatasetTransformer transformer;
	private final DatasetRepository repository;
	private final SubContractedProviderRepository subContractedProviderRepository;

	@Autowired
	public DatasetServiceImpl(
			DatasetRepository repository,
			SubContractedProviderRepository subContractedProviderRepository,
			DatasetTransformer transformer)
	{
		this.repository = repository;
		this.subContractedProviderRepository = subContractedProviderRepository;
		this.transformer = transformer;
	}

	@Override
	public List<Dataset> getDatasets()
	{
		return repository.findAllBySelectable("Y").stream()
				.map(transformer::map)
				.collect(toList());
	}

	@Override
	public List<Dataset> getDatasets(String username)
	{
		return repository.findAllByUserLinks_User_Username(username).stream()
				.map(transformer::map)
				.collect(toList());
	}

	@Override
	public List<String> getDatasetCodes(String username)
	{
		return getDatasets(username).stream().map(Dataset::getCode).collect(toList());
	}

	@Override
	public Optional<Long> getDatasetId(String code)
	{
		return repository.findByCode(code).map(ProbationAreaEntity::getId);
	}

	@Override
	public Optional<Dataset> getDatasetByCode(String code)
	{
		return repository.findByCode(code).map(transformer::map);
	}

	@Override
	public String getNextStaffCode(String datasetCode)
	{
		return repository.getNextStaffCode(datasetCode);
	}

	@Override
	public Optional<Long> getOrganisationIdByDatasetCode(String code)
	{
		return repository.findByCode(code).map(ProbationAreaEntity::getOrganisationId);
	}

	@Override
	public List<Dataset> getSubContractedProviders(String datasetCode) {
		return subContractedProviderRepository.findAllByProviderCode(datasetCode).stream()
				.map(transformer::map)
				.collect(toList());
	}

	@Override
	public Optional<Long> getSubContractedProviderId(String code) {
		return subContractedProviderRepository.findByCode(code).map(SubContractedProviderEntity::getId);
	}
}
