package uk.co.bconline.ndelius.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import uk.co.bconline.ndelius.security.filter.PreAuthenticatedRequestParameterFilter;
import uk.co.bconline.ndelius.service.impl.OAuthClientDetailsService;

@Configuration
@EnableAuthorizationServer
public class OAuthServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${delius.secret:#{null}}")
	private String deliusSecret;

	private final OAuthClientDetailsService clientDetailsService;
	private final RedisConnectionFactory redisConnectionFactory;
	private final PasswordEncoder passwordEncoder;
	private OAuth2RequestFactory oauth2RequestFactory;

	public OAuthServerConfig(
			OAuthClientDetailsService clientDetailsService,
			RedisConnectionFactory redisConnectionFactory,
			PasswordEncoder passwordEncoder
	) {
		this.clientDetailsService = clientDetailsService;
		this.redisConnectionFactory = redisConnectionFactory;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.tokenStore(redisTokenStore());
		this.oauth2RequestFactory = endpoints.getOAuth2RequestFactory();
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security.realm("ndelius-oauth")
				.passwordEncoder(passwordEncoder)
				.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()")
				.addTokenEndpointAuthenticationFilter(
						new PreAuthenticatedRequestParameterFilter(oauth2RequestFactory, deliusSecret));
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetailsService);
	}

	@Bean
	public TokenStore redisTokenStore() {
		return new RedisTokenStore(redisConnectionFactory);
	}
}
