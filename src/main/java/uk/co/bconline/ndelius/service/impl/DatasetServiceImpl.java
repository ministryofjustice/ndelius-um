package uk.co.bconline.ndelius.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;
import uk.co.bconline.ndelius.repository.db.DatasetRepository;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.transformer.DatasetTransformer;

@Service
public class DatasetServiceImpl implements DatasetService
{
	private final DatasetTransformer transformer;
	private final DatasetRepository repository;

	@Autowired
	public DatasetServiceImpl(
			DatasetRepository repository,
			DatasetTransformer transformer)
	{
		this.repository = repository;
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
		return repository.findAllByUsersWithDatasetUsername(username).stream()
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

}
