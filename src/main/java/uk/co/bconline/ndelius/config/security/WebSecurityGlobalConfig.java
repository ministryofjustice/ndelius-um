package uk.co.bconline.ndelius.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityGlobalConfig extends WebSecurityConfigurerAdapter
{
	@Autowired
	public void configureGlobal(OIDUserDetailsService userDetailsService,
			AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
	{
		authenticationManagerBuilder
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
				if (!encodedPassword.startsWith("{"))
				{
					// OID passes back the password as a stringify'd byte array, so we manually unpick it and turn it
					// back into a hashed string for verification here.
					String[] split = encodedPassword.split(",");
					byte[] bytes = new byte[split.length];
					for (int i = 0; i < split.length; i++)
					{
						bytes[i] = Byte.valueOf(split[i]);
					}
					encodedPassword = new String(bytes);
				}
				return super.matches(rawPassword, encodedPassword);
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
				registry.addMapping("/**");
			}
		};
	}
}
