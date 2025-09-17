package uk.co.bconline.ndelius.config.security;

import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uk.co.bconline.ndelius.config.security.converter.ScopeFilteringAuthorizationCodeRequestConverter;

@Configuration
public class AuthorizationServerConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
        HttpSecurity http,
        AuthenticationConfiguration authenticationConfiguration,
        @Qualifier("userEntryServiceImpl") UserDetailsService userDetailsService
    ) throws Exception {
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        val authenticationManager = authenticationConfiguration.getAuthenticationManager();
        val basicAuthenticationFilter = new BasicAuthenticationFilter(authenticationManager);

        return http
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher()))
            .userDetailsService(userDetailsService)
            .with(authorizationServerConfigurer, server -> server
                .authorizationEndpoint(endpoint -> endpoint.authorizationRequestConverter(new ScopeFilteringAuthorizationCodeRequestConverter())))
            .formLogin(formLogin -> formLogin.loginPage("/login").permitAll())
            .addFilterBefore(basicAuthenticationFilter, LogoutFilter.class) // To ensure basic authentication is applied before OAuthAuthorizationEndpointFilter
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .build();
    }

    @Bean
    public DelegatingOAuth2TokenGenerator delegatingOAuth2TokenGenerator() {
        return new DelegatingOAuth2TokenGenerator(new OAuth2AccessTokenGenerator(), new OAuth2RefreshTokenGenerator());
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new InMemoryOAuth2AuthorizationService(); // TODO replace with Redis
    }

//
//	@Value("${delius.secret:#{null}}")
//	private String deliusSecret;
//

    /*
     * By default Spring updates the Redis configuration to enable 'notify-keyspace-events' for session expiration.
     * However in secure environments, the Redis CONFIG endpoints are disabled. In this scenario the configuration
     * should be updated ourselves and the Spring auto-configuration should be disabled.
     *
     * This bean allows us to conditionally disable the Spring auto-configuration.
     *
     * See: https://github.com/spring-projects/spring-session/issues/124
     */
    @Bean
    @ConditionalOnProperty("redis.configure.no-op")
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

//	private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
//		// Append the token granter for the 'preauthenticated' grant_type to the list of oauth token granters
//		return new CompositeTokenGranter(asList(endpoints.getTokenGranter(),
//				new PreAuthenticatedTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
//						userDetailsService, endpoints.getOAuth2RequestFactory(), deliusSecret)));
//	}
//
private CorsConfigurationSource corsConfigurationSource() {
    val source = new UrlBasedCorsConfigurationSource();
    val config = new CorsConfiguration();
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("POST");
    source.registerCorsConfiguration("/**", config);
    return source;
}
}
