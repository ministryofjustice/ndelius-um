package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.User;

public class HomeAreaNotMatchingStaffCodeValidator implements ConstraintValidator<HomeAreaNotMatchingStaffCode, User>
{
	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		String staffCode = user.getStaffCode();
		Dataset homeArea = user.getHomeArea();

		if (staffCode != null && homeArea != null && !isEmpty(homeArea.getCode()))
		{
			return staffCode.startsWith(homeArea.getCode());
		}
		else
		{
			return true;
		}
	}
}
