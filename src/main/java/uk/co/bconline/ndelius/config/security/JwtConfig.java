package uk.co.bconline.ndelius.config.security;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser
		.NullAuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
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
@Order(1)
@Configuration
public class JwtConfig extends WebSecurityConfigurerAdapter
{
	private final JwtHelper jwtHelper;
	private final RequestMatcher loginRequestMatcher;
	private final LoginHandler loginHandler;

	@Autowired
	public JwtConfig(JwtHelper jwtHelper, RequestMatcher loginRequestMatcher, LoginHandler loginHandler)
	{
		this.jwtHelper = jwtHelper;
		this.loginRequestMatcher = loginRequestMatcher;
		this.loginHandler = loginHandler;
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception
	{
		httpSecurity
				.sessionManagement()
					.sessionCreationPolicy(STATELESS)
					.and()
				.exceptionHandling()
					.authenticationEntryPoint(jwtEntryPoint())
					.and()
				.addFilterBefore(jwtAuthenticationFilter(), BasicAuthenticationFilter.class)
				.authenticationProvider(jwtAuthenticationProvider())
				.authorizeRequests()
					.antMatchers(OPTIONS).permitAll()
					.antMatchers("/actuator/**").permitAll()
					.requestMatchers(new NegatedRequestMatcher(loginRequestMatcher)).authenticated()
					.and()
				.csrf().disable();
		httpSecurity.headers().frameOptions().disable();
	}

	@Bean
	public OncePerRequestFilter jwtAuthenticationFilter()
	{
		return new OncePerRequestFilter()
		{
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
						val user = userDetailsService().loadUserByUsername(username);
						val authentication = new AuthenticationToken(user, token);
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
					catch (JwtException e)
					{
						log.error("Unable to accept JWT token", e);
						val failed = new InsufficientAuthenticationException(
								e instanceof ExpiredJwtException? "Expired token": "Invalid token", e);
						loginHandler.onAuthenticationFailure(request, response, failed);
						if (!loginRequestMatcher.matches(request))
						{
							jwtEntryPoint().commence(request, response, failed);
							return;
						}
					}
				}
				else if (!loginRequestMatcher.matches(request) && !"OPTIONS".equals(request.getMethod())
						&& !new AntPathRequestMatcher("/actuator/**").matches(request))
				{
					val failed = new InsufficientAuthenticationException("Missing token");
					loginHandler.onAuthenticationFailure(request, response, failed);
					jwtEntryPoint().commence(request, response, new InsufficientAuthenticationException("Missing token"));
					return;
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	@Bean
	public AuthenticationEntryPoint jwtEntryPoint()
	{
		return (request, response, e) -> {
			response.setHeader(WWW_AUTHENTICATE, "Bearer");
			response.sendError(SC_UNAUTHORIZED, e.getMessage());
		};
	}

	@Bean
	public AuthenticationProvider jwtAuthenticationProvider()
	{
		return new NullAuthenticationProvider();
	}
}
