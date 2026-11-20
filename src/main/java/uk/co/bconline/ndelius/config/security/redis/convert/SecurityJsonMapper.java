package uk.co.bconline.ndelius.config.security.redis.convert;

import org.springframework.security.jackson.SecurityJacksonModules;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2AuthorizationGrantAuthorization;

final class SecurityJsonMapper {

    private SecurityJsonMapper() {
    }

    static ObjectMapper create() {
        return JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(SecurityJsonMapper.class.getClassLoader()))
            .build();
    }

    static ObjectMapper forClaims() {
        var typeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(OAuth2AuthorizationGrantAuthorization.ClaimsHolder.class);
        return JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(SecurityJsonMapper.class.getClassLoader(), typeValidator))
            .addMixIn(OAuth2AuthorizationGrantAuthorization.ClaimsHolder.class, ClaimsHolderMixin.class)
            .build();
    }
}
