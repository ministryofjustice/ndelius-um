package uk.co.bconline.ndelius.config.security;

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.val;
import uk.co.bconline.ndelius.security.LoginSuccessHandler;
import uk.co.bconline.ndelius.service.impl.AD1UserDetailsService;

@Configuration
@Order(2)
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
	private final LoginSuccessHandler loginSuccessHandler;

	@Autowired
	public SpnegoConfig(AD1UserDetailsService userDetailsService, RequestMatcher loginRequestMatcher,
			LoginSuccessHandler loginSuccessHandler)
	{
		this.userDetailsService = userDetailsService;
		this.loginRequestMatcher = loginRequestMatcher;
		this.loginSuccessHandler = loginSuccessHandler;
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
				.addFilterBefore(spnegoAuthenticationProcessingFilter(), BasicAuthenticationFilter.class)
				.authenticationProvider(kerberosAuthenticationProvider())
				.authenticationProvider(kerberosServiceAuthenticationProvider())
				.authorizeRequests()
					.antMatchers(OPTIONS).permitAll()
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
	public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter() throws Exception
	{
		SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter()
		{
			@Override
			public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
					throws IOException, ServletException
			{
				HttpServletRequest request = (HttpServletRequest) req;
				HttpServletResponse response = (HttpServletResponse) res;
				if ("OPTIONS".equals(request.getMethod()) || shouldNotFilter(request)) {
					filterChain.doFilter(request, response);
				} else {
					Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
					if (existingAuth != null && !existingAuth.isAuthenticated()) {
						filterChain.doFilter(request, response);
						return;
					}

					String header = request.getHeader("Authorization");
					if (header != null && (header.startsWith("Negotiate ") || header.startsWith("Kerberos ")))
					{
						super.doFilter(request, response, filterChain);
					} else {
						spnegoEntryPoint().commence(request, response, null);
					}
				}
			}
		};
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setSuccessHandler(loginSuccessHandler);
		return filter;
	}

	private boolean shouldNotFilter(HttpServletRequest request)
	{
		val auth = SecurityContextHolder.getContext().getAuthentication();
		return !loginRequestMatcher.matches(request) ||
				(auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated());
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
		ticketValidator.setDebug(true);
		return ticketValidator;
	}
}
