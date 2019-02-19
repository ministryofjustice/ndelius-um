package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import lombok.val;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, User>
{
	@Autowired
	private OIDUserDetailsService oidUserDetailsService;

	@Override
	@Transactional
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		String email = user.getEmail();
		String username = user.getUsername();

		if (isEmpty(email) || isEmpty(username))
			return true;

		val searchedUsername = oidUserDetailsService.getUsernameByEmail(email);
		return searchedUsername.map(username::equals).orElse(true);
	}

}
