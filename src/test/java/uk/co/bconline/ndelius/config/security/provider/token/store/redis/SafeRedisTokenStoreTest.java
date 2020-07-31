package uk.co.bconline.ndelius.config.security.provider.token.store.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNull;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class SafeRedisTokenStoreTest {

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	private static class FailToDeserializeTokenStrategy extends JdkSerializationStrategy {
		@Override
		protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
			throw new SerializationFailedException("Failed to deserialize payload");
		}
	}

	@Test
	public void deserializationErrorCausesTokenToBeRemoved() throws Exception {
		var tokenStore = new SaferRedisTokenStore(redisConnectionFactory);
		var tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(tokenStore);

		// Given a user authenticates and their token is stored in the token store
		var auth = new OAuth2Authentication(new AuthorizationRequest().createOAuth2Request(), new UsernamePasswordAuthenticationToken("username", "password"));
		var token = tokenServices.createAccessToken(auth);
		tokenStore.storeAccessToken(token, auth);

		// But the token can no longer be deserialized (eg. due to a release with class redefinitions)
		tokenStore.setSerializationStrategy(new FailToDeserializeTokenStrategy());

		// When they authenticate with the same credentials
		var newToken = tokenStore.getAccessToken(auth);

		// Then there was no deserialization exception thrown
		// And the old token has been removed
		assertNull(newToken);
	}

}
