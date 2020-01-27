package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.stream.Stream;

import static uk.co.bconline.ndelius.util.Constants.NATIONAL_ACCESS;

@Slf4j
@UtilityClass
public class AuthUtils
{
	public static Authentication me()
	{
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static String myUsername()
	{
		val principal = me().getPrincipal();
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		} else if (principal instanceof ClientDetails) {
			return ((ClientDetails) principal).getClientId();
		}
		return (String) principal;
	}

	public static boolean isClient()
	{
		if (me() instanceof OAuth2Authentication) {
			return "client_credentials".equalsIgnoreCase(((OAuth2Authentication) me()).getOAuth2Request().getGrantType());
		}
		return false;
	}

	public static String myToken()
	{
		val details = me().getDetails();
		if (details instanceof OAuth2AuthenticationDetails) {
			return ((OAuth2AuthenticationDetails) details).getTokenValue();
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
		log.debug("Checking national access (UABI025), interactions={}", myInteractions());
		return myInteractions().anyMatch(NATIONAL_ACCESS::equals);
	}
}
