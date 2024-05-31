package uk.co.bconline.ndelius.validator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entry.RoleEntry;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.service.UserRoleService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@Slf4j
public class AssignableRolesValidator implements ConstraintValidator<AssignableRoles, User>
{
	@Autowired
	private UserEntryService userService;

	@Autowired
	private UserRoleService userRoleService;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		val newRoles = ofNullable(user.getRoles()).orElse(emptyList()).stream()
				.map(Role::getName)
				.collect(toSet());

		if (newRoles.isEmpty()) return true;

		val assignableRoles = userRoleService.getRolesICanAssign().stream()
				.map(RoleEntry::getName).collect(toSet());
		userService.getUser(ofNullable(user.getExistingUsername()).orElse(user.getUsername()))
				.ifPresent(u -> assignableRoles.addAll(u.getRoles().stream().map(RoleEntry::getName).collect(toSet())));

		return assignableRoles.containsAll(newRoles);
	}
}
