package uk.co.bconline.ndelius.validator;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.model.Role;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.RoleService;
import uk.co.bconline.ndelius.service.UserService;

@Slf4j
public class AssignableRolesValidator implements ConstraintValidator<AssignableRoles, User>
{
	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		val newRoles = ofNullable(user.getRoles()).orElse(emptyList()).stream()
				.map(Role::getName)
				.collect(toSet());

		if (newRoles.isEmpty()) return true;

		val assignableRoles = roleService.getRoles();
		userService.getUser(user.getUsername()).ifPresent(u -> assignableRoles.addAll(u.getRoles()));

		return assignableRoles.stream()
				.map(Role::getName)
				.collect(toSet())
				.containsAll(newRoles);
	}
}
