package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import uk.co.bconline.ndelius.model.Dataset;
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
		Dataset homeArea = user.getHomeArea();

		if (staffCode == null)
		{
			return (teams == null || teams.isEmpty()) &&
					(staffGrade == null || isEmpty(staffGrade.getCode()));
		}
		else
		{
			if (staffGrade != null && !isEmpty(staffGrade.getCode()) &&
					homeArea != null && !isEmpty(homeArea.getCode()))
			{
				return staffCode.startsWith(homeArea.getCode());
			}
			return false;
		}
	}
}
