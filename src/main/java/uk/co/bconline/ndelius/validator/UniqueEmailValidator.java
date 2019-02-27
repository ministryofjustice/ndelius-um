package uk.co.bconline.ndelius.validator;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.isEmpty;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, User>
{
	@Autowired
	private OIDUserDetailsService oidUserDetailsService;

	@Override
	@Transactional
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		String email = user.getEmail();
		String username = ofNullable(user.getExistingUsername()).orElse(user.getUsername());

		if (isEmpty(email) || isEmpty(username))
			return true;

		val searchedUsername = oidUserDetailsService.getUsernameByEmail(email);
		return searchedUsername.map(username::equals).orElse(true);
	}

}
