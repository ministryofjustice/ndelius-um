package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.stream.Stream;

import static uk.co.bconline.ndelius.util.Constants.NATIONAL_ACCESS;

@UtilityClass
public class AuthUtils
{
	public static Authentication me()
	{
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static String myUsername()
	{
		return (String) me().getPrincipal();
	}

	public static String myToken()
	{
		val details = me().getDetails();
		if (details instanceof OAuth2AuthenticationDetails) {
			return ((OAuth2AuthenticationDetails) me().getDetails()).getTokenValue();
		} else {
			return null;
		}
	}

	public static Stream<String> myInteractions()
	{
		return me().getAuthorities().stream().map(GrantedAuthority::getAuthority);
	}

	public static boolean isNational()
	{
		return myInteractions().anyMatch(NATIONAL_ACCESS::equals);
	}
}
