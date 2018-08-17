package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.val;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

public class UniqueAliasUsernameValidator implements ConstraintValidator<UniqueAliasUsername, User>
{
	@Autowired
	private OIDUserDetailsService oidUserDetailsService;

	@Override
	public boolean isValid(User user, ConstraintValidatorContext context)
	{
		String username = user.getUsername();
		String aliasUsername = user.getAliasUsername();

		if (isEmpty(aliasUsername) || isEmpty(username) || aliasUsername.equals(username))
		{
			return true;
		}

		val searchedUsername = oidUserDetailsService.getUsernameByAlias(aliasUsername);
		return searchedUsername.map(username::equals).orElse(true);
	}
}