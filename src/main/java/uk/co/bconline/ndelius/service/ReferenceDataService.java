package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.ReferenceData;

import java.util.List;
import java.util.Optional;

public interface ReferenceDataService {
    List<ReferenceData> getStaffGrades();

    Optional<Long> getStaffGradeId(String code);
}
