package uk.co.bconline.ndelius.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.co.bconline.ndelius.model.User;

public class MissingTeamsValidator implements ConstraintValidator<MissingTeams, User>
{
	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		if (user.getTeams() != null)
		{
			return user.getStaffCode() != null && (user.getTeams() != null || !user.getTeams().isEmpty());
		}
		return true;
	}
}