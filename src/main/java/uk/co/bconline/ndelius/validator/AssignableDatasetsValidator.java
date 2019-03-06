package uk.co.bconline.ndelius.validator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.ProbationAreaEntity;
import uk.co.bconline.ndelius.service.DBUserService;
import uk.co.bconline.ndelius.service.DatasetService;
import uk.co.bconline.ndelius.service.OIDUserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.Constants.NATIONAL_ACCESS;

@Slf4j
public class AssignableDatasetsValidator implements ConstraintValidator<AssignableDatasets, User>
{
	@Autowired
	private DBUserService dbUserService;

	@Autowired
	private OIDUserService oidUserService;

	@Autowired
	private DatasetService datasetService;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		val myPrincipal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		val isNational = myPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.anyMatch(NATIONAL_ACCESS::equals);
		if (isNational) return true;

		val newDatasets = ofNullable(user.getDatasets()).orElse(emptyList()).stream()
				.filter(Objects::nonNull)
				.map(Dataset::getCode)
				.filter(s -> s != null && !s.isEmpty())
				.collect(toSet());

		if (newDatasets.isEmpty()) return true;

		val assignableDatasets = datasetService.getDatasets(myPrincipal.getUsername()).stream()
				.map(Dataset::getCode)
				.collect(toSet());
		assignableDatasets.add(oidUserService.getUserHomeArea(myPrincipal.getUsername()));

		dbUserService.getUser(ofNullable(user.getExistingUsername()).orElse(user.getUsername())).ifPresent(
				u -> assignableDatasets.addAll(u.getDatasets().stream()
						.map(ProbationAreaEntity::getCode)
						.collect(toSet())));

		return assignableDatasets.containsAll(newDatasets);
	}
}
