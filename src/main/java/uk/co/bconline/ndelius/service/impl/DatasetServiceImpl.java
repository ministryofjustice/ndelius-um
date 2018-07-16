package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;
import uk.co.bconline.ndelius.repository.db.DatasetRepository;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.transformer.DatasetTransformer;

import java.util.List;

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
		List<ProbationAreaEntity> datasetsDB = repository.findAllBySelectable("Y");
		return transformer.map(datasetsDB);
	}
}
