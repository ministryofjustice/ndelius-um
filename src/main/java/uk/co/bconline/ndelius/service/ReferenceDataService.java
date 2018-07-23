package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.ReferenceData;

public interface ReferenceDataService
{
	List<ReferenceData> getStaffGrades();
	Optional<Long> getStaffGradeId(String code);
}
