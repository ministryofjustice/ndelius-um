package uk.co.bconline.ndelius.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.co.bconline.ndelius.util.LdapUtils;

import static org.springframework.web.cors.CorsConfiguration.ALL;

@Configuration
public class ResourceServerConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods(ALL);
			}
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new LdapShaPasswordEncoder()
		{
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword)
			{
				return encodedPassword != null && super.matches(rawPassword, LdapUtils.fixPassword(encodedPassword));
			}
		};
	}
}
