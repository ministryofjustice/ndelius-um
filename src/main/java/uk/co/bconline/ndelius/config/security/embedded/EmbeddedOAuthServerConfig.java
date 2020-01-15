package uk.co.bconline.ndelius.config.security.embedded;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import uk.co.bconline.ndelius.config.security.embedded.EmbeddedOAuthServerConfigProperties.EmbeddedOAuthClient;

@Slf4j
@Configuration
@EnableAuthorizationServer
@ConditionalOnProperty("security.oauth2.embedded.enabled")
public class EmbeddedOAuthServerConfig extends AuthorizationServerConfigurerAdapter {
	private final EmbeddedOAuthServerConfigProperties configProperties;
	private final PasswordEncoder passwordEncoder;

	public EmbeddedOAuthServerConfig(
			EmbeddedOAuthServerConfigProperties configProperties,
			PasswordEncoder passwordEncoder) {
		this.configProperties = configProperties;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
		oauthServer
				.realm("embedded-oauth")
				.passwordEncoder(passwordEncoder)
				.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer config) throws Exception {
		log.debug("Configuring embedded OAuth server");
		ClientDetailsServiceBuilder inMemory = config.inMemory();
		for (EmbeddedOAuthClient client: configProperties.getClients()) {
			log.debug("Adding embedded OAuth client: {}", client.getId());
			inMemory = inMemory
					.withClient(client.getId())
					.secret(passwordEncoder.encode(client.getSecret()))
					.authorizedGrantTypes(client.getAuthorizedGrantTypes().toArray(new String[0]))
					.scopes(client.getScopes().toArray(new String[0]))
					.authorities(client.getScopes().toArray(new String[0]))
					.autoApprove(client.isAutoApprove())
					.and();
		}
	}
}
