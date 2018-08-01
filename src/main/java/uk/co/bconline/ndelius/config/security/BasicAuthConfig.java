package uk.co.bconline.ndelius.config.security;

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.val;
import uk.co.bconline.ndelius.security.LoginHandler;

@Configuration
@Order(2)
@ConditionalOnProperty(name = "spnego.enabled", havingValue = "false", matchIfMissing = true)
public class BasicAuthConfig extends WebSecurityConfigurerAdapter
{
	private final RequestMatcher loginRequestMatcher;

	@Autowired
	public BasicAuthConfig(RequestMatcher loginRequestMatcher)
	{
		this.loginRequestMatcher = loginRequestMatcher;
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception
	{
		httpSecurity
				.sessionManagement()
					.sessionCreationPolicy(STATELESS)
					.and()
				.exceptionHandling()
					.authenticationEntryPoint(basicEntryPoint())
					.and()
				.addFilter(basicAuthenticationFilter())
				.authorizeRequests()
					.antMatchers(OPTIONS).permitAll()
					.requestMatchers(loginRequestMatcher).authenticated()
					.and()
				.csrf().disable();
	}

	@Bean
	public BasicAuthenticationFilter basicAuthenticationFilter() throws Exception
	{
		return new BasicAuthenticationFilter(authenticationManager(), basicEntryPoint())
		{
			@Autowired
			private LoginHandler loginHandler;

			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws IOException, ServletException
			{
				String header = request.getHeader("Authorization");
				if ("OPTIONS".equals(request.getMethod()) || (header != null && header.startsWith("Basic ")))
				{
					super.doFilterInternal(request, response, filterChain);
				} else {
					basicEntryPoint().commence(request, response, null);
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
				return !loginRequestMatcher.matches(request) ||
						(auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated());
			}
		};
	}

	@Bean
	public BasicAuthenticationEntryPoint basicEntryPoint()
	{
		BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		entryPoint.setRealmName("ndelius-um");
		return entryPoint;
	}
}
