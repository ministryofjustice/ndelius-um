package uk.co.bconline.ndelius.validator;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.co.bconline.ndelius.model.ReferenceData;
import uk.co.bconline.ndelius.model.Team;
import uk.co.bconline.ndelius.model.User;

public class ValidStaffCodeValidator implements ConstraintValidator<ValidStaffCode, User>
{

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		String staffCode = user.getStaffCode();
		List<Team> teams = user.getTeams();
		ReferenceData staffGrade = user.getStaffGrade();

		if (staffCode == null)
		{
			return true;
		}
		else
		{
			if(teams != null && staffGrade != null)
			{
				return !teams.isEmpty() && !staffGrade.getCode().isEmpty() && !staffGrade.getDescription()
						.isEmpty();
			}
		}
		return false;
	}
}
