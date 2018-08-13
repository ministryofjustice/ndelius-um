package uk.co.bconline.ndelius.validator;

import static org.springframework.util.CollectionUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import uk.co.bconline.ndelius.model.User;

public class MissingTeamsValidator implements ConstraintValidator<MissingTeams, User>
{
	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		if(!isEmpty(user.getTeams()))
		{
			return !StringUtils.isEmpty(user.getStaffCode());
		}
		return true;
	}
}