package uk.co.bconline.ndelius.config.security;

import static org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.val;
import uk.co.bconline.ndelius.security.filter.BasicAuthFilter;
import uk.co.bconline.ndelius.security.handler.LoginHandler;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;
import uk.co.bconline.ndelius.util.LdapPasswordUtils;

@Configuration
@EnableWebSecurity
public class WebSecurityGlobalConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	public void configureGlobal(OIDUserDetailsService userDetailsService,
			AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
	{
		authenticationManagerBuilder
				.authenticationProvider(preAuthenticatedAuthenticationProvider())
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new LdapShaPasswordEncoder()
		{
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword)
			{
				return encodedPassword != null && super.matches(rawPassword, LdapPasswordUtils.fixPassword(encodedPassword));
			}
		};
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception
	{
		return super.authenticationManagerBean();
	}

	@Bean
	public RequestMatcher loginRequestMatcher()
	{
		return new OrRequestMatcher(
				new NegatedRequestMatcher(new AntPathRequestMatcher("/api/**")),	// static resources
				new AntPathRequestMatcher("/api/login"));							// login endpoint
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE", "HEAD")
						.allowCredentials(true);
			}
		};
	}

	@Bean
	public BasicAuthFilter basicAuthenticationFilter(LoginHandler loginHandler) throws Exception
	{
		return new BasicAuthFilter(loginHandler, loginRequestMatcher(), authenticationManager(), basicEntryPoint());
	}

	@Bean
	public BasicAuthenticationEntryPoint basicEntryPoint()
	{
		BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
		entryPoint.setRealmName("ndelius-um");
		return entryPoint;
  }

	@Bean
	public MethodInvokingFactoryBean methodInvokingFactoryBean()
	{
		// Set strategy for SecurityContextHolder to 'inheritable', so auth details can be retrieved in async threads
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
		methodInvokingFactoryBean.setTargetMethod("setStrategyName");
		methodInvokingFactoryBean.setArguments(MODE_INHERITABLETHREADLOCAL);
		return methodInvokingFactoryBean;
	}

	@Bean
	public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider()
	{
		val provider = new PreAuthenticatedAuthenticationProvider();
		provider.setPreAuthenticatedUserDetailsService(
				token -> userDetailsService().loadUserByUsername((String) token.getPrincipal()));
		return provider;
	}
}
