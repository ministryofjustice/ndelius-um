package uk.co.bconline.ndelius.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(2)
public class WebSecurityConfig {
    @Bean
    SecurityFilterChain uiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/login")
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated())
            .formLogin(formLogin -> formLogin.loginPage("/login").permitAll())
            .httpBasic(httpBasic -> httpBasic.realmName("ndelius-users"))
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .build();
    }
}
