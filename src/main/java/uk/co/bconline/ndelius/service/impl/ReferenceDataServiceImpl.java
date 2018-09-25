package uk.co.bconline.ndelius.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.model.entity.ReferenceDataEntity;
import uk.co.bconline.ndelius.repository.db.ReferenceDataRepository;
import uk.co.bconline.ndelius.service.ReferenceDataService;
import uk.co.bconline.ndelius.transformer.ReferenceDataTransformer;

@Service
public class ReferenceDataServiceImpl implements ReferenceDataService
{
	private static final String OFFICER_GRADE = "OFFICER GRADE";

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
	@Cacheable(value = "staffGrades", key = "'all'")
	public List<ReferenceData> getStaffGrades()
	{
		List<ReferenceDataEntity> resultsDB = repository.findAllBySelectableAndReferenceDataMasterCodeSetName("Y", OFFICER_GRADE);
		return transformer.map(resultsDB);
	}

	@Override
	@Cacheable("staffGradeIds")
	public Optional<Long> getStaffGradeId(String code)
	{
		return repository.findByCodeAndReferenceDataMasterCodeSetName(code, OFFICER_GRADE).map(ReferenceDataEntity::getId);
	}
}
