package uk.co.bconline.ndelius.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.kerberos.authentication.KerberosServiceRequestToken;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import lombok.Setter;

@Setter
public class SpnegoWithFallbackFilter extends SpnegoAuthenticationProcessingFilter
{
	private AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationManager authenticationManager;
	private AuthenticationSuccessHandler successHandler;
	private AuthenticationFailureHandler failureHandler;
	private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();
	private boolean skipIfAlreadyAuthenticated = true;

	private final AuthenticationEntryPoint entryPoint;
	private final RequestMatcher loginRequestMatcher;

	public SpnegoWithFallbackFilter(
			AuthenticationEntryPoint entryPoint,
			RequestMatcher loginRequestMatcher)
	{
		this.entryPoint = entryPoint;
		this.loginRequestMatcher = loginRequestMatcher;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		if (shouldNotFilter(request))
		{
			chain.doFilter(request, response);
			return;
		}

		if (skipIfAlreadyAuthenticated)
		{
			Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();

			if (existingAuth != null && existingAuth.isAuthenticated()
					&& !(existingAuth instanceof AnonymousAuthenticationToken))
			{
				chain.doFilter(request, response);
				return;
			}
		}

		String header = request.getHeader("Authorization");
		if (header == null)
		{
			entryPoint.commence(request, response,
					new AuthenticationCredentialsNotFoundException("Missing SSO header"));
			return;
		}

		if (header.startsWith("Negotiate ") || header.startsWith("Kerberos "))
		{
			if (logger.isDebugEnabled()) {
				logger.debug("Received Negotiate Header for request " + request.getRequestURL() + ": " + header);
			}
			byte[] base64Token = header.substring(header.indexOf(" ") + 1).getBytes("UTF-8");
			byte[] kerberosTicket = Base64.decode(base64Token);
			KerberosServiceRequestToken authenticationRequest = new KerberosServiceRequestToken(kerberosTicket);
			authenticationRequest.setDetails(authenticationDetailsSource.buildDetails(request));
			Authentication authentication;
			try
			{
				authentication = authenticationManager.authenticate(authenticationRequest);
				sessionStrategy.onAuthentication(authentication, request, response);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				if (successHandler != null)
				{
					successHandler.onAuthenticationSuccess(request, response, authentication);
				}
			} catch (AuthenticationException e) {
				logger.warn("Negotiate Header was invalid: " + header, e);
				SecurityContextHolder.clearContext();
				if (failureHandler != null) {
					failureHandler.onAuthenticationFailure(request, response, e);
				}
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		super.setAuthenticationManager(authenticationManager);
	}

	@Override
	public void afterPropertiesSet() throws ServletException
	{
		super.afterPropertiesSet();
		Assert.notNull(this.authenticationManager, "authenticationManager must be specified");
	}

	public boolean shouldNotFilter(HttpServletRequest request)
	{
		return "OPTIONS".equals(request.getMethod()) || !loginRequestMatcher.matches(request);
	}
}
