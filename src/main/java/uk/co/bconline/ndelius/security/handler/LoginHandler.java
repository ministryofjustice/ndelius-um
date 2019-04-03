package uk.co.bconline.ndelius.security.handler;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.auth.UserInteraction;
import uk.co.bconline.ndelius.model.auth.UserPrincipal;
import uk.co.bconline.ndelius.security.AuthenticationToken;
import uk.co.bconline.ndelius.service.UserRoleService;
import uk.co.bconline.ndelius.util.JwtHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j(topic = "audit")
public class LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler
{
	@Value("${jwt.expiry}")
	private int expiry;

	@Value("${jwt.cookie}")
	private String cookieName;

	private final JwtHelper jwtHelper;
	private final UserRoleService userRoleService;

	@Autowired
	public LoginHandler(JwtHelper jwtHelper,
						UserRoleService userRoleService)
	{
		this.jwtHelper = jwtHelper;
		this.userRoleService = userRoleService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
	{
		val username = ((UserDetails) authentication.getPrincipal()).getUsername();
		generateToken(username, response);
		log.info("{} [UMLOGIN] []", username);
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
	{
		log.error("??? [UMLOGIN] [{}, {}, {}]", request.getRequestURI(), request.getHeader(AUTHORIZATION), exception.getMessage());
	}

	public void generateToken(String username, HttpServletResponse response)
	{
		val interactions = userRoleService.getUserInteractions(username).stream()
				.map(UserInteraction::new)
				.collect(toList());
		val user = UserPrincipal.builder()
				.username(username)
				.authorities(interactions)
				.build();

		// Add token to response cookie
		val token = jwtHelper.generateToken(username, interactions);
		jwtHelper.addTokenToResponse(token, response);

		// Set authentication in context
		val authentication = new AuthenticationToken(user, token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
