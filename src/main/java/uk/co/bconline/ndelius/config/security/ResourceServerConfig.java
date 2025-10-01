package uk.co.bconline.ndelius.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import uk.co.bconline.ndelius.util.LdapUtils;

@Configuration
public class ResourceServerConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll())
            .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(Customizer.withDefaults()))
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new LdapShaPasswordEncoder() {
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword != null && super.matches(rawPassword, LdapUtils.fixPassword(encodedPassword));
            }
        };
    }
}
