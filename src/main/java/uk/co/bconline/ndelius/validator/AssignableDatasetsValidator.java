package uk.co.bconline.ndelius.validator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.service.UserEntityService;
import uk.co.bconline.ndelius.service.UserEntryService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.AuthUtils.isNational;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
public class AssignableDatasetsValidator implements ConstraintValidator<AssignableDatasets, User>
{
	@Autowired
	private UserEntityService userEntityService;

	@Autowired
	private UserEntryService userEntryService;

	@Autowired
	private DatasetService datasetService;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		if (isNational()) return true;

		val newDatasets = ofNullable(user.getDatasets()).orElse(emptyList()).stream()
				.filter(Objects::nonNull)
				.map(Dataset::getCode)
				.filter(s -> s != null && !s.isEmpty())
				.collect(toSet());

		if (newDatasets.isEmpty()) return true;

		val assignableDatasets = datasetService.getDatasets(myUsername()).stream()
				.map(Dataset::getCode)
				.collect(toSet());
		assignableDatasets.add(userEntryService.getUserHomeArea(myUsername()));

		userEntityService.getUser(ofNullable(user.getExistingUsername()).orElse(user.getUsername())).ifPresent(
				u -> assignableDatasets.addAll(u.getDatasets().stream()
						.map(ProbationAreaEntity::getCode)
						.collect(toSet())));

		return assignableDatasets.containsAll(newDatasets);
	}
}
