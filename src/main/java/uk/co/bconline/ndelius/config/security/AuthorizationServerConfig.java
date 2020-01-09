package uk.co.bconline.ndelius.config.security;

import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import uk.co.bconline.ndelius.config.security.provider.code.RedisAuthorizationCodeServices;
import uk.co.bconline.ndelius.config.security.provider.endpoint.PathMatchRedirectResolver;
import uk.co.bconline.ndelius.config.security.provider.token.PreAuthenticatedTokenGranter;

import static java.util.Arrays.asList;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${delius.secret:#{null}}")
	private String deliusSecret;

	private final AuthenticationManager authenticationManager;
	private final ClientDetailsService clientDetailsService;
	private final UserDetailsService userDetailsService;
	private final RedisConnectionFactory redisConnectionFactory;
	private final PasswordEncoder passwordEncoder;
	private final PathMatchRedirectResolver pathMatchRedirectResolver;

	public AuthorizationServerConfig(
			AuthenticationManager authenticationManager,
			@Qualifier("clientEntryServiceImpl") ClientDetailsService clientDetailsService,
			@Qualifier("userEntryServiceImpl") UserDetailsService userDetailsService,
			RedisConnectionFactory redisConnectionFactory,
			PasswordEncoder passwordEncoder,
			PathMatchRedirectResolver pathMatchRedirectResolver
	) {
		this.authenticationManager = authenticationManager;
		this.clientDetailsService = clientDetailsService;
		this.userDetailsService = userDetailsService;
		this.redisConnectionFactory = redisConnectionFactory;
		this.passwordEncoder = passwordEncoder;
		this.pathMatchRedirectResolver = pathMatchRedirectResolver;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService)
				.tokenStore(redisTokenStore())
				.authorizationCodeServices(redisAuthorizationCodeServices())
				.tokenGranter(tokenGranter(endpoints))
				.redirectResolver(pathMatchRedirectResolver);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security.realm("ndelius-clients")
				.passwordEncoder(passwordEncoder)
				.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()")
				.addTokenEndpointAuthenticationFilter(new CorsFilter(corsConfigurationSource()));
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

	@Bean
	public TokenStore redisTokenStore() {
		return new RedisTokenStore(redisConnectionFactory);
	}

	@Bean
	public RedisAuthorizationCodeServices redisAuthorizationCodeServices() {
		return new RedisAuthorizationCodeServices(redisConnectionFactory);
	}

	private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
		// Append the token granter for the 'preauthenticated' grant_type to the list of oauth token granters
		return new CompositeTokenGranter(asList(endpoints.getTokenGranter(),
				new PreAuthenticatedTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
						endpoints.getOAuth2RequestFactory(), deliusSecret)));
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
