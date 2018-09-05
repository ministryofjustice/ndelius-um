package uk.co.bconline.ndelius.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.security.AuthenticationToken;
import uk.co.bconline.ndelius.security.handler.LoginHandler;
import uk.co.bconline.ndelius.util.JwtHelper;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter
{
	private final JwtHelper jwtHelper;
	private final UserDetailsService userDetailsService;
	private final LoginHandler loginHandler;
	private final AuthenticationEntryPoint jwtEntryPoint;
	private final RequestMatcher loginRequestMatcher;

	public JwtAuthFilter(JwtHelper jwtHelper,
			UserDetailsService userDetailsService,
			LoginHandler loginHandler,
			AuthenticationEntryPoint jwtEntryPoint,
			RequestMatcher loginRequestMatcher)
	{
		this.jwtHelper = jwtHelper;
		this.userDetailsService = userDetailsService;
		this.loginHandler = loginHandler;
		this.jwtEntryPoint = jwtEntryPoint;
		this.loginRequestMatcher = loginRequestMatcher;
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
				val username = jwtHelper.getUsernameFromToken(token);
				val user = userDetailsService.loadUserByUsername(username);
				val authentication = new AuthenticationToken(user, token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			catch (JwtException e)
			{
				log.error("Unable to accept JWT token", e);
				val failed = new InsufficientAuthenticationException(
						e instanceof ExpiredJwtException ? "Expired token": "Invalid token", e);
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
