package uk.co.bconline.ndelius.validator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.transformer.GroupTransformer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.AuthUtils.isNational;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Slf4j
public class AssignableGroupsValidator implements ConstraintValidator<AssignableGroups, User>
{
	@Autowired
	private UserEntryService userEntryService;

	@Autowired
	private GroupTransformer groupTransformer;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		// National admins are not restricted
		if (isNational()) return true;

		// Get the set of new groups that I intend to assign to the user
		val newGroups = ofNullable(user.getGroups()).orElse(emptyMap()).values().stream()
				.flatMap(List::stream)
				.collect(toSet());
		if (newGroups.isEmpty()) return true;

		// Get the groups that are either assigned to myself or are already assigned to the user
		val assignableGroups = userEntryService.getUserGroups(myUsername());
		val existingGroups = userEntryService.getUserGroups(ofNullable(user.getExistingUsername()).orElse(user.getUsername()));
		assignableGroups.addAll(existingGroups);

		// If all the new groups being assigned are assignable by myself (ie. already assigned to myself, or already
		// assigned to the user) - then this assignment is valid.
		return groupTransformer.map(assignableGroups).containsAll(newGroups);
	}
}
