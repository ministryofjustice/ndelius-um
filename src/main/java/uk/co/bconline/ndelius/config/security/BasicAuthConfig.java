package uk.co.bconline.ndelius.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import uk.co.bconline.ndelius.security.filter.BasicAuthFilter;

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Order(3)
@Configuration
public class BasicAuthConfig extends WebSecurityConfigurerAdapter
{
	private final BasicAuthFilter filter;
	private final BasicAuthenticationEntryPoint entryPoint;
	private final RequestMatcher loginRequestMatcher;

	@Autowired
	public BasicAuthConfig(
			BasicAuthFilter filter,
			BasicAuthenticationEntryPoint entryPoint,
			RequestMatcher loginRequestMatcher)
	{
		this.filter = filter;
		this.entryPoint = entryPoint;
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
					.authenticationEntryPoint(entryPoint)
					.and()
				.addFilter(filter)
				.authorizeRequests()
					.antMatchers(OPTIONS).permitAll()
					.antMatchers("/actuator/**").permitAll()
					.requestMatchers(loginRequestMatcher).authenticated()
					.and()
				.csrf().disable();
	}
}
