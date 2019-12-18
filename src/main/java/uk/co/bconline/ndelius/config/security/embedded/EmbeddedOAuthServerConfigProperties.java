package uk.co.bconline.ndelius.config.security.embedded;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("security.oauth2.embedded")
public class EmbeddedOAuthServerConfigProperties {
	private boolean enabled;
	private List<EmbeddedOAuthClient> clients = new ArrayList<>();

	@Data
	static class EmbeddedOAuthClient {
		private String id;
		private String secret;
		private List<String> authorizedGrantTypes;
		private List<String> scopes;
		private boolean autoApprove;
	}
}
