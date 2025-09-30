package uk.co.bconline.ndelius.config.security.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import uk.co.bconline.ndelius.config.security.redis.convert.BytesToClaimsHolderConverter;
import uk.co.bconline.ndelius.config.security.redis.convert.BytesToOAuth2AuthorizationRequestConverter;
import uk.co.bconline.ndelius.config.security.redis.convert.BytesToUsernamePasswordAuthenticationTokenConverter;
import uk.co.bconline.ndelius.config.security.redis.convert.ClaimsHolderToBytesConverter;
import uk.co.bconline.ndelius.config.security.redis.convert.OAuth2AuthorizationRequestToBytesConverter;
import uk.co.bconline.ndelius.config.security.redis.convert.UsernamePasswordAuthenticationTokenToBytesConverter;
import uk.co.bconline.ndelius.config.security.redis.repository.OAuth2AuthorizationGrantAuthorizationRepository;
import uk.co.bconline.ndelius.config.security.redis.repository.OAuth2UserConsentRepository;
import uk.co.bconline.ndelius.config.security.redis.service.RedisOAuth2AuthorizationConsentService;
import uk.co.bconline.ndelius.config.security.redis.service.RedisOAuth2AuthorizationService;

import java.util.Arrays;

@Configuration(proxyBeanMethods = false)
@EnableRedisRepositories
public class RedisConfig {
    @Bean
    @Order(1)
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(
            Arrays.asList(
                new UsernamePasswordAuthenticationTokenToBytesConverter(),
                new BytesToUsernamePasswordAuthenticationTokenConverter(),
                new OAuth2AuthorizationRequestToBytesConverter(),
                new BytesToOAuth2AuthorizationRequestConverter(),
                new ClaimsHolderToBytesConverter(),
                new BytesToClaimsHolderConverter()
            )
        );
    }

    @Bean
    @Order(2)
    public RedisOAuth2AuthorizationService authorizationService(RegisteredClientRepository registeredClientRepository,
                                                                OAuth2AuthorizationGrantAuthorizationRepository authorizationGrantAuthorizationRepository) {
        return new RedisOAuth2AuthorizationService(registeredClientRepository, authorizationGrantAuthorizationRepository);
    }

    @Bean
    @Order(3)
    public RedisOAuth2AuthorizationConsentService authorizationConsentService(OAuth2UserConsentRepository userConsentRepository) {
        return new RedisOAuth2AuthorizationConsentService(userConsentRepository);
    }
}
