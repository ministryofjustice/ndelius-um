package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Stream;

import static uk.co.bconline.ndelius.util.Constants.NATIONAL_ACCESS;

@UtilityClass
public class AuthUtils
{
	public static UserDetails me()
	{
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public static String myUsername()
	{
		return me().getUsername();
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
