package uk.co.bconline.ndelius.validator;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.co.bconline.ndelius.model.User;

public class ValidDatesValidator implements ConstraintValidator<ValidDates, User>
{

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		LocalDate startDate = user.getStartDate();
		LocalDate endDate = user.getEndDate();

		if (startDate != null && endDate != null)
		{
			return startDate.isBefore(endDate) || startDate.isEqual(endDate);
		}
		else
		{
			return true;
		}
	}
}
