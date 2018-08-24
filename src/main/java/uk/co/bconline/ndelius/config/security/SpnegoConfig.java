package uk.co.bconline.ndelius.config.security;

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import uk.co.bconline.ndelius.security.filter.BasicAuthFilter;
import uk.co.bconline.ndelius.security.filter.SpnegoWithFallbackFilter;
import uk.co.bconline.ndelius.security.handler.LoginHandler;
import uk.co.bconline.ndelius.service.impl.AD1UserDetailsService;

@Order(2)
@Configuration
@ConditionalOnProperty("spnego.enabled")
public class SpnegoConfig extends WebSecurityConfigurerAdapter
{
	@Value("${spnego.service-principal}")
	private String servicePrincipal;

	@Value("${spnego.keytab}")
	private String keytab;

	@Value("${spnego.debug:false}")
	private boolean debug;

	private final AD1UserDetailsService userDetailsService;
	private final RequestMatcher loginRequestMatcher;
	private final LoginHandler loginHandler;
	private final BasicAuthFilter basicAuthFilter;

	@Autowired
	public SpnegoConfig(
			AD1UserDetailsService userDetailsService,
			RequestMatcher loginRequestMatcher,
			LoginHandler loginHandler,
			BasicAuthFilter basicAuthFilter)
	{
		this.userDetailsService = userDetailsService;
		this.loginRequestMatcher = loginRequestMatcher;
		this.loginHandler = loginHandler;
		this.basicAuthFilter = basicAuthFilter;
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception
	{
		httpSecurity
				.sessionManagement()
					.sessionCreationPolicy(STATELESS)
					.and()
				.exceptionHandling()
					.authenticationEntryPoint(spnegoEntryPoint())
					.and()
				.addFilterBefore(spnegoFilter(), BasicAuthenticationFilter.class)
				.addFilter(basicAuthFilter)
				.authenticationProvider(kerberosAuthenticationProvider())
				.authenticationProvider(kerberosServiceAuthenticationProvider())
				.authorizeRequests()
					.antMatchers(OPTIONS).permitAll()
					.antMatchers("/actuator/**").permitAll()
					.requestMatchers(loginRequestMatcher).authenticated()
					.and()
				.csrf().disable();
	}

	@Bean
	public KerberosAuthenticationProvider kerberosAuthenticationProvider()
	{
		KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
		SunJaasKerberosClient client = new SunJaasKerberosClient();
		client.setDebug(debug);
		provider.setKerberosClient(client);
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

	@Bean
	public AuthenticationEntryPoint spnegoEntryPoint()
	{
		return new SpnegoEntryPoint();
	}

	@Bean
	public SpnegoWithFallbackFilter spnegoFilter() throws Exception
	{
		SpnegoWithFallbackFilter filter = new SpnegoWithFallbackFilter(spnegoEntryPoint(), loginRequestMatcher);
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setSuccessHandler(loginHandler);
		return filter;
	}

	@Bean
	public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider()
	{
		KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
		provider.setTicketValidator(sunJaasKerberosTicketValidator());
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

	@Bean
	public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator()
	{
		SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
		ticketValidator.setServicePrincipal(servicePrincipal);
		ticketValidator.setKeyTabLocation(new FileSystemResource(keytab));
		ticketValidator.setDebug(debug);
		return ticketValidator;
	}
}
