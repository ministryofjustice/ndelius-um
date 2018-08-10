package uk.co.bconline.ndelius.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.val;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.model.entity.UserEntity;
import uk.co.bconline.ndelius.service.impl.DBUserDetailsService;

public class UniqueStaffCodeValidator implements ConstraintValidator<UniqueStaffCode, User>
{
	@Autowired
	private DBUserDetailsService dbUserDetailsService;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		String staffCode = user.getStaffCode();
		String username = user.getUsername();

		if(staffCode != null){
			val searchedUser = dbUserDetailsService.getUserByStaffCode(staffCode);
			return searchedUser.map(UserEntity::getUsername).map(username::equalsIgnoreCase).orElse(true);
		}else{
			return true;
		}
	}
}
