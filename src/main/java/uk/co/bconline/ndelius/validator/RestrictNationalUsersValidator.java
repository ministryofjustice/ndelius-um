package uk.co.bconline.ndelius.validator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameNotFoundException;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserRoleService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Optional.ofNullable;
import static uk.co.bconline.ndelius.util.AuthUtils.isNational;
import static uk.co.bconline.ndelius.util.Constants.NATIONAL_ACCESS;

@Slf4j
public class RestrictNationalUsersValidator implements ConstraintValidator<RestrictNationalUsers, User>
{
	@Autowired
	private UserRoleService userRoleService;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		val username = ofNullable(user.getExistingUsername()).orElse(user.getUsername());

		try {
			// I am a national admin OR I am updating a non-national (local) user => valid
			return isNational() || !userRoleService.getUserInteractions(username).contains(NATIONAL_ACCESS);
		} catch (NameNotFoundException e) {
			// user doesn't exist => valid
			return true;
		}
	}
}
