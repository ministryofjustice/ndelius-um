package uk.co.bconline.ndelius.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.bconline.ndelius.model.auth.UserPrincipal;
import uk.co.bconline.ndelius.security.AuthenticationToken;
import uk.co.bconline.ndelius.security.handler.LoginHandler;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;
import uk.co.bconline.ndelius.util.JwtHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;

import static java.time.Instant.now;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter
{
	private final JwtHelper jwtHelper;
	private final LoginHandler loginHandler;
	private final AuthenticationEntryPoint jwtEntryPoint;
	private final RequestMatcher loginRequestMatcher;
	private final OIDUserDetailsService oidUserDetailsService;

	public JwtAuthFilter(JwtHelper jwtHelper,
						 LoginHandler loginHandler,
						 AuthenticationEntryPoint jwtEntryPoint,
						 RequestMatcher loginRequestMatcher,
						 OIDUserDetailsService oidUserDetailsService)
	{
		this.jwtHelper = jwtHelper;
		this.loginHandler = loginHandler;
		this.jwtEntryPoint = jwtEntryPoint;
		this.loginRequestMatcher = loginRequestMatcher;
		this.oidUserDetailsService = oidUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException
	{
		val token = jwtHelper.getToken(request);
		if (token != null)
		{
			try
			{
				val claims = jwtHelper.parseToken(token);
				val username = claims.getSubject();

				if (!oidUserDetailsService.usernameExists(username)) {
					throw new JwtException("Subject no longer exists");
				}

				val user = UserPrincipal.builder()
						.username(username)
						.authorities(jwtHelper.getInteractions(claims))
						.build();

				// set security context for request
				val authentication = new AuthenticationToken(user, token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			catch (ExpiredJwtException e)
			{
				log.debug("Token has expired, attempting to refresh user details");
				val username = e.getClaims().getSubject();
				val expiredAt = e.getClaims().getExpiration().toInstant();
				if (expiredAt.isAfter(now().minus(24, ChronoUnit.HOURS)))
				{
					loginHandler.generateToken(username, request, response);
				}
				else
				{
					log.debug("Token is older than 24 hours, re-asserting authentication", e);
					val failed = new InsufficientAuthenticationException("Expired token", e);
					loginHandler.onAuthenticationFailure(request, response, failed);
					if (!loginRequestMatcher.matches(request))
					{
						jwtEntryPoint.commence(request, response, failed);
						return;
					}
				}
			}
			catch (JwtException e)
			{
				log.error("Unable to accept JWT token", e);
				val failed = new InsufficientAuthenticationException("Invalid token", e);
				loginHandler.onAuthenticationFailure(request, response, failed);
				if (!loginRequestMatcher.matches(request))
				{
					jwtEntryPoint.commence(request, response, failed);
					return;
				}
			}
		}
		else if (!loginRequestMatcher.matches(request) && !"OPTIONS".equals(request.getMethod())
				&& !new AntPathRequestMatcher("/actuator/**").matches(request))
		{
			val failed = new InsufficientAuthenticationException("Missing token");
			loginHandler.onAuthenticationFailure(request, response, failed);
			jwtEntryPoint.commence(request, response, new InsufficientAuthenticationException("Missing token"));
			return;
		}
		filterChain.doFilter(request, response);
	}
}
