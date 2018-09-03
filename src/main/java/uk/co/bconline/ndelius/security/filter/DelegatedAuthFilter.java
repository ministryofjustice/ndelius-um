package uk.co.bconline.ndelius.security.filter;

import static uk.co.bconline.ndelius.util.EncryptionUtils.decrypt;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.security.handler.LoginHandler;

@Slf4j
public class DelegatedAuthFilter extends OncePerRequestFilter
{
	@Value("${delius.secret}")
	private String secret;

	private final LoginHandler loginHandler;
	private final RequestMatcher loginRequestMatcher;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public DelegatedAuthFilter(
			LoginHandler loginHandler,
			RequestMatcher loginRequestMatcher,
			AuthenticationManager authenticationManager)
	{
		this.loginHandler = loginHandler;
		this.loginRequestMatcher = loginRequestMatcher;
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException
	{
		val timestamp = decrypt(request.getParameter("t"), secret);
		val username = decrypt(request.getParameter("u"), secret);
		if (timestamp == null || username == null)
		{
			val e = new BadCredentialsException("Unable to decrypt request parameters");
			log.debug(e.getMessage(), e);
			throw e;
		}

		if (Instant.ofEpochMilli(Long.valueOf(timestamp))
				.isBefore(Instant.now().minus(2, ChronoUnit.HOURS)))
		{
			val e = new NonceExpiredException("Timestamp expired");
			log.debug(String.format("%s - username=%s, timestamp=%s", e.getMessage(), username, timestamp), e);
			throw e;
		}

		val auth = authenticationManager.authenticate(new PreAuthenticatedAuthenticationToken(username, timestamp));
		loginHandler.onAuthenticationSuccess(request, response, auth);

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request)
	{
		val auth = SecurityContextHolder.getContext().getAuthentication();
		return "OPTIONS".equals(request.getMethod()) || new AntPathRequestMatcher("/actuator/**").matches(request)
				|| !loginRequestMatcher.matches(request)
				|| (auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated())
				|| StringUtils.isEmpty(request.getParameter("u"))
				|| StringUtils.isEmpty(request.getParameter("t"));
	}
}
