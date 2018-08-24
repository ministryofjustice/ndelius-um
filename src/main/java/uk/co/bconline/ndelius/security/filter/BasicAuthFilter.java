package uk.co.bconline.ndelius.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.val;
import uk.co.bconline.ndelius.security.handler.LoginHandler;

public class BasicAuthFilter extends BasicAuthenticationFilter
{
	private LoginHandler loginHandler;
	private RequestMatcher loginRequestMatcher;

	public BasicAuthFilter(
			LoginHandler loginHandler,
			RequestMatcher loginRequestMatcher,
			AuthenticationManager authenticationManager,
			AuthenticationEntryPoint authenticationEntryPoint)
	{
		super(authenticationManager, authenticationEntryPoint);
		this.loginHandler = loginHandler;
		this.loginRequestMatcher = loginRequestMatcher;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException
	{
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Basic "))
		{
			super.doFilterInternal(request, response, filterChain);
		}
		else
		{
			getAuthenticationEntryPoint().commence(request, response, null);
		}
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication)
	{
		loginHandler.onAuthenticationSuccess(request, response, authentication);
	}

	@Override
	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) {
		loginHandler.onAuthenticationFailure(request, response, failed);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request)
	{
		val auth = SecurityContextHolder.getContext().getAuthentication();
		return "OPTIONS".equals(request.getMethod()) || new AntPathRequestMatcher("/actuator/**").matches(request)
				|| !loginRequestMatcher.matches(request)
				|| (auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated());
	}
}
