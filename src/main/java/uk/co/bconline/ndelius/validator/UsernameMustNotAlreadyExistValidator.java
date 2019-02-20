package uk.co.bconline.ndelius.validator;

import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class UsernameMustNotAlreadyExistValidator implements ConstraintValidator<UsernameMustNotAlreadyExist, Object[]>
{
	@Autowired
	private UserService userService;

	@Override
	public boolean isValid(Object[] params, ConstraintValidatorContext context)
	{
		String username = ((User) params[0]).getUsername();

		return !userService.usernameExists(username);
	}
}
