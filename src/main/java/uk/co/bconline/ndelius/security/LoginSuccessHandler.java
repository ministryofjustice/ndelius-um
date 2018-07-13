package uk.co.bconline.ndelius.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.util.JwtHelper;

@Component
@Slf4j(topic = "audit")
public class LoginSuccessHandler implements AuthenticationSuccessHandler
{
	@Value("${jwt.expiry}")
	private int expiry;

	@Value("${jwt.cookie}")
	private String cookieName;

	private final JwtHelper jwtHelper;

	@Autowired
	public LoginSuccessHandler(JwtHelper jwtHelper)
	{
		this.jwtHelper = jwtHelper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	{
		val user = (UserDetails) authentication.getPrincipal();
		val token = jwtHelper.generateToken(user.getUsername());
		authentication = new AuthenticationToken(user, token);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Add token to response cookie
		val cookie = new Cookie(cookieName, token);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(expiry * 60);
		cookie.setPath("/");
		response.addCookie(cookie);

		log.info("{} {}", user.getUsername(), "[UMLOGIN] []");
	}
}
