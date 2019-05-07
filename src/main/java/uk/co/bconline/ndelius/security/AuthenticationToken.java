package uk.co.bconline.ndelius.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AuthenticationToken extends AbstractAuthenticationToken
{
	private transient final Object principal;
	private transient final Object credentials;

	public AuthenticationToken(UserDetails principal, String credentials)
	{
		super(principal.getAuthorities());
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(true);
	}
}
