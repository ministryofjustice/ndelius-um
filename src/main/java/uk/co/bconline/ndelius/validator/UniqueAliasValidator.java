package uk.co.bconline.ndelius.validator;

import static org.springframework.util.StringUtils.isEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.val;
import uk.co.bconline.ndelius.model.Alias;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

public class UniqueAliasValidator implements ConstraintValidator<UniqueAliasUsername, Alias>
{
	@Autowired
	private OIDUserDetailsService oidUserDetailsService;

	@Override
	public boolean isValid(Alias alias, ConstraintValidatorContext context)
	{
		String username = alias.getUsername();
		String aliasUsername = alias.getAliasUsername();

		if (isEmpty(aliasUsername) || isEmpty(username) || aliasUsername.equals(username))
		{
			return true;
		}

		val searchedUsername = oidUserDetailsService.getUsernameByAlias(aliasUsername);
		return searchedUsername.map(username::equals).orElse(true);
	}
}
