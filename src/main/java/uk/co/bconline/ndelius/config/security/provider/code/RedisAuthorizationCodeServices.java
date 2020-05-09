package uk.co.bconline.ndelius.config.security.provider.code;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

import java.util.List;

/**
 * Implementation of authorization code services that stores the codes and authentication in Redis.
 *
 * See:
 *  https://github.com/spring-projects/spring-security-oauth/issues/935
 *  https://github.com/spring-projects/spring-security-oauth/pull/936
 * NOTE: This is merged and should be available in spring-security-oauth:2.4.2.RELEASE. This class can be removed after upgrading.
 *
 * 
 * @author Stefan Rempfer
 */
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

	private static final String AUTH_CODE = "auth_code:";

	private final RedisConnectionFactory connectionFactory;

	private String prefix = "";

	private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

	/**
	 * Default constructor.
	 *
	 * @param connectionFactory the connection factory which should be used to obtain a connection to Redis
	 */
	public RedisAuthorizationCodeServices(RedisConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	protected void store(String code, OAuth2Authentication authentication) {
		byte[] key = serializeKey(AUTH_CODE + code);
		byte[] auth = serialize(authentication);

		RedisConnection conn = getConnection();
		try {
			conn.set(key, auth);
		}
		finally {
			conn.close();
		}
	}

	@Override
	protected OAuth2Authentication remove(String code) {
		byte[] key = serializeKey(AUTH_CODE + code);

		List<Object> results = null;
		RedisConnection conn = getConnection();
		try {
			conn.openPipeline();
			conn.get(key);
			conn.del(key);
			results = conn.closePipeline();
		}
		finally {
			conn.close();
		}

		if (results == null) {
			return null;
		}
		byte[] bytes = (byte[]) results.get(0);
		return deserializeAuthentication(bytes);
	}

	private byte[] serializeKey(String object) {
		return serialize(prefix + object);
	}

	private byte[] serialize(Object object) {
		return serializationStrategy.serialize(object);
	}

	private byte[] serialize(String string) {
		return serializationStrategy.serialize(string);
	}

	private RedisConnection getConnection() {
		return connectionFactory.getConnection();
	}

	private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
		return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
	}

	public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
		this.serializationStrategy = serializationStrategy;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}