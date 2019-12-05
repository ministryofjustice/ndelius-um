package uk.co.bconline.ndelius.config.security;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser.NullAuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import uk.co.bconline.ndelius.security.filter.DelegatedAuthFilter;
import uk.co.bconline.ndelius.security.filter.JwtAuthFilter;
import uk.co.bconline.ndelius.security.handler.LoginHandler;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.util.JwtHelper;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@Order(1)
@Configuration
public class JwtConfig extends WebSecurityConfigurerAdapter
{
	private final JwtHelper jwtHelper;
	private final RequestMatcher loginRequestMatcher;
	private final LoginHandler loginHandler;
	private final UserEntryService userEntryService;

	@Autowired
	public JwtConfig(JwtHelper jwtHelper,
					 RequestMatcher loginRequestMatcher,
					 LoginHandler loginHandler,
					 UserEntryService userEntryService)
	{
		this.jwtHelper = jwtHelper;
		this.loginRequestMatcher = loginRequestMatcher;
		this.loginHandler = loginHandler;
		this.userEntryService = userEntryService;
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
				.addFilterBefore(delegatedAuthFilter(), BasicAuthenticationFilter.class)
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
	public JwtAuthFilter jwtAuthenticationFilter()
	{
		return new JwtAuthFilter(jwtHelper, loginHandler, jwtEntryPoint(), loginRequestMatcher, userEntryService);
	}

	@Bean
	public DelegatedAuthFilter delegatedAuthFilter() throws Exception
	{
		return new DelegatedAuthFilter(loginHandler, loginRequestMatcher, authenticationManager());
	}

	@Bean
	public AuthenticationEntryPoint jwtEntryPoint()
	{
		return (request, response, e) -> {
			response.setHeader(WWW_AUTHENTICATE, "Bearer");
			response.sendError(SC_UNAUTHORIZED, e.getMessage());
			// clear session cookie
			val cookie = jwtHelper.createCookie("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		};
	}

	@Bean
	public AuthenticationProvider jwtAuthenticationProvider()
	{
		return new NullAuthenticationProvider();
	}
}
