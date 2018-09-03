package uk.co.bconline.ndelius.config.security;

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import uk.co.bconline.ndelius.security.filter.DelegatedAuthFilter;
import uk.co.bconline.ndelius.security.handler.LoginHandler;

@Order(2)
@Configuration
@ConditionalOnProperty("delius.secret")
public class DelegatedAuthConfig extends WebSecurityConfigurerAdapter
{
	private final LoginHandler loginHandler;
	private final RequestMatcher loginRequestMatcher;

	@Autowired
	public DelegatedAuthConfig(
			LoginHandler loginHandler,
			RequestMatcher loginRequestMatcher)
	{
		this.loginHandler = loginHandler;
		this.loginRequestMatcher = loginRequestMatcher;
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception
	{
		httpSecurity
				.sessionManagement()
					.sessionCreationPolicy(STATELESS)
					.and()
				.addFilterBefore(delegatedAuthFilter(), BasicAuthenticationFilter.class)
				.authorizeRequests()
					.antMatchers(OPTIONS).permitAll()
					.antMatchers("/actuator/**").permitAll()
					.requestMatchers(loginRequestMatcher).authenticated()
					.and()
				.csrf().disable();
	}

	@Bean
	public DelegatedAuthFilter delegatedAuthFilter() throws Exception
	{
		return new DelegatedAuthFilter(loginHandler, loginRequestMatcher, authenticationManager());
	}
}
