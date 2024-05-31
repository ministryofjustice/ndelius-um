package uk.co.bconline.ndelius.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Order(1)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig  {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login", "/oauth/authorize")
						.permitAll()
						.anyRequest().authenticated()).formLogin(form -> form.loginPage("/login").permitAll())
						.httpBasic(basic -> basic.realmName("ndelius-users"))
						.headers(options -> options.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

		return http.build();
	}
}
