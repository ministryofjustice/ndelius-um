package uk.co.bconline.ndelius.config.security.provider.token.store.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

/**
 * SafeRedisTokenStore. Ensures that users can still re-authenticate if the stored classes are redefined (eg. on a deployment containing changes to the UserDetails implementation)..
 */
@Slf4j
public class SaferRedisTokenStore extends RedisTokenStore {

	private static final String AUTH_TO_ACCESS = "auth_to_access:";

	private RedisConnectionFactory connectionFactory;
	private String prefix;
	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
	private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

	public SaferRedisTokenStore(RedisConnectionFactory connectionFactory) {
		super(connectionFactory);
		this.connectionFactory = connectionFactory;
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		try {
			return super.getAccessToken(authentication);
		} catch (SerializationFailedException e) {
			String key = authenticationKeyGenerator.extractKey(authentication);
			log.debug("Unable to deserialize auth-to-access token", e);
			log.info("Unable to deserialize auth-to-access token. Removing '{}' from store", prefix + AUTH_TO_ACCESS + key);
			byte[] serializedKey = serializationStrategy.serialize(prefix + AUTH_TO_ACCESS + key);
			try (RedisConnection conn = connectionFactory.getConnection()) {
				conn.del(serializedKey);
			}
			return null;
		}
	}

	public void setPrefix(String prefix) {
		super.setPrefix(prefix);
		this.prefix = prefix;
	}

	public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
		super.setAuthenticationKeyGenerator(authenticationKeyGenerator);
		this.authenticationKeyGenerator = authenticationKeyGenerator;
	}

	public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
		super.setSerializationStrategy(serializationStrategy);
		this.serializationStrategy = serializationStrategy;
	}
}
