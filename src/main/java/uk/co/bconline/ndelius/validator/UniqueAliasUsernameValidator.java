package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.UnexpectedTypeException;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.val;
import uk.co.bconline.ndelius.model.Alias;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

public class UniqueAliasUsernameValidator implements ConstraintValidator<UniqueAliasUsername, Object>
{
	@Autowired
	private OIDUserDetailsService oidUserDetailsService;

	@Override
	public boolean isValid(Object user, ConstraintValidatorContext context)
	{
		String username;
		String aliasUsername;
		if (user instanceof User)
		{
			username = ((User) user).getUsername();
			aliasUsername = ((User) user).getUsername();
		}
		else if (user instanceof Alias)
		{
			username = ((Alias) user).getUsername();
			aliasUsername = ((Alias) user).getUsername();
		} else throw new UnexpectedTypeException();

		if (isEmpty(aliasUsername) || isEmpty(username) || aliasUsername.equals(username))
		{
			return true;
		}

		val searchedUsername = oidUserDetailsService.getUsernameByAlias(aliasUsername);
		return searchedUsername.map(username::equals).orElse(true);
	}
}
