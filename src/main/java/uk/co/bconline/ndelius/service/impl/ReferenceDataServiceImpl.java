package uk.co.bconline.ndelius.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;
import uk.co.bconline.ndelius.repository.db.ReferenceDataRepository;
import uk.co.bconline.ndelius.service.ReferenceDataService;
import uk.co.bconline.ndelius.transformer.ReferenceDataTransformer;

import java.util.List;

@Service
public class ReferenceDataServiceImpl implements ReferenceDataService
{
	private final ReferenceDataTransformer transformer;
	private final ReferenceDataRepository repository;

	@Autowired
	public ReferenceDataServiceImpl(
			ReferenceDataRepository repository,
			ReferenceDataTransformer transformer)
	{
		this.repository = repository;
		this.transformer = transformer;
	}

	@Override
	public List<ReferenceData> getStaffGrades()
	{
		List<ReferenceDataEntity> resultsDB = repository.findAllBySelectableAndReferenceDataMasterCodeSetName("Y", "OFFICER GRADE");
		return transformer.map(resultsDB);
	}
}
