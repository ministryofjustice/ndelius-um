package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.co.bconline.ndelius.model.User;

public class MissingStaffGradeValidator implements ConstraintValidator<MissingStaffGrade, User>
{
	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		if (user.getStaffGrade() != null)
		{
			return user.getStaffCode() != null && (user.getStaffGrade() != null || !isEmpty(user.getStaffGrade().getCode()));
		}
		return true;
	}
}