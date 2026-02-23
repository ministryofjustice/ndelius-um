package uk.co.bconline.ndelius.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uk.co.bconline.ndelius.config.security.converter.PreAuthenticatedGrantAuthenticationConverter;
import uk.co.bconline.ndelius.config.security.converter.PreAuthenticatedGrantPublicClientAuthenticationConverter;
import uk.co.bconline.ndelius.config.security.converter.ScopeFilteringAuthorizationCodeRequestConverter;
import uk.co.bconline.ndelius.config.security.handler.ContextRelativeRedirectAuthorizationEndpointSuccessHandler;
import uk.co.bconline.ndelius.config.security.provider.PreAuthenticatedGrantAuthenticationProvider;
import uk.co.bconline.ndelius.config.security.provider.PreAuthenticatedGrantPublicClientAuthenticationProvider;

@Configuration
public class AuthorizationServerConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(
        HttpSecurity http,
        AuthenticationConfiguration authenticationConfiguration,
        @Qualifier("userEntryServiceImpl") UserDetailsService userDetailsService,
        @Value("${delius.secret}") String deliusSecret,
        RegisteredClientRepository registeredClientRepository,
        OAuth2AuthorizationService authorizationService
    ) throws Exception {
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer();
        authorizationServerConfigurer.init(http);
        val authenticationManager = authenticationConfiguration.getAuthenticationManager();
        val tokenGenerator = http.getSharedObject(OAuth2TokenGenerator.class);
        val preAuthenticatedGrantAuthenticationConverter = new PreAuthenticatedGrantAuthenticationConverter();
        val preAuthenticatedGrantAuthenticationProvider = new PreAuthenticatedGrantAuthenticationProvider(deliusSecret, registeredClientRepository, tokenGenerator, authorizationService, userDetailsService);
        val preAuthenticatedGrantPublicClientAuthenticationConverter = new PreAuthenticatedGrantPublicClientAuthenticationConverter();
        val preAuthenticatedGrantPublicClientAuthenticationProvider = new PreAuthenticatedGrantPublicClientAuthenticationProvider(registeredClientRepository);
        val clientCredentialsAuthenticationProvider = new OAuth2ClientCredentialsAuthenticationProvider(authorizationService, tokenGenerator);

        return http
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.ignoringRequestMatchers(authorizationServerConfigurer.getEndpointsMatcher()))
            .userDetailsService(userDetailsService)
            .with(authorizationServerConfigurer, server -> server
                .clientAuthentication(clientAuthentication -> clientAuthentication
                    .authenticationConverter(preAuthenticatedGrantPublicClientAuthenticationConverter)
                    .authenticationProvider(preAuthenticatedGrantPublicClientAuthenticationProvider)
                    .authenticationProvider(clientCredentialsAuthenticationProvider))
                .tokenEndpoint(endpoint -> endpoint
                    .accessTokenRequestConverter(preAuthenticatedGrantAuthenticationConverter)
                    .authenticationProvider(preAuthenticatedGrantAuthenticationProvider))
                .authorizationEndpoint(endpoint -> endpoint
                    .authorizationResponseHandler(new ContextRelativeRedirectAuthorizationEndpointSuccessHandler())
                    .authorizationRequestConverter(new ScopeFilteringAuthorizationCodeRequestConverter())))
            .formLogin(formLogin -> formLogin.loginPage("/login").permitAll())
            .addFilterBefore(new BasicAuthenticationFilter(authenticationManager){
                @Override
                protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
                    return !"/oauth/authorize".equals(request.getPathInfo());
                }
            }, LogoutFilter.class) // To ensure basic authentication is applied before OAuthAuthorizationEndpointFilter
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        // to match values from Spring Boot 2's authorization server library:
        return AuthorizationServerSettings.builder()
            .authorizationEndpoint("/oauth/authorize")
            .pushedAuthorizationRequestEndpoint("/oauth/par")
            .deviceAuthorizationEndpoint("/oauth/device_authorization")
            .deviceVerificationEndpoint("/oauth/device_verification")
            .tokenEndpoint("/oauth/token")
            .tokenIntrospectionEndpoint("/oauth/check_token")
            .tokenRevocationEndpoint("/oauth/revoke")
            .jwkSetEndpoint("/oauth/jwks")
            .build();
    }

    @Bean
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> tokenCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType()) && context.getPrincipal() != null) {
                // Add a custom "user_name" claim to match Spring Boot 2's authorization server behaviour
                context.getClaims().claim("user_name", context.getPrincipal().getName());
            }
        };
    }

    /*
     * By default, Spring updates the Redis configuration to enable 'notify-keyspace-events' for session expiration.
     * However in secure environments, the Redis CONFIG endpoints are disabled. In this scenario the configuration
     * should be updated ourselves and the Spring autoconfiguration should be disabled.
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
