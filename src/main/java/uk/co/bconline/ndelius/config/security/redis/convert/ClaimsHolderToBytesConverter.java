package uk.co.bconline.ndelius.config.security.redis.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2AuthorizationGrantAuthorization;

@WritingConverter
public class ClaimsHolderToBytesConverter implements Converter<OAuth2AuthorizationGrantAuthorization.ClaimsHolder, byte[]> {

    private final Jackson2JsonRedisSerializer<OAuth2AuthorizationGrantAuthorization.ClaimsHolder> serializer;

    public ClaimsHolderToBytesConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(SecurityJackson2Modules.getModules(ClaimsHolderToBytesConverter.class.getClassLoader()));
        objectMapper.registerModules(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.addMixIn(OAuth2AuthorizationGrantAuthorization.ClaimsHolder.class, ClaimsHolderMixin.class);
        this.serializer = new Jackson2JsonRedisSerializer<>(objectMapper, OAuth2AuthorizationGrantAuthorization.ClaimsHolder.class);
    }

    @Override
    public byte[] convert(OAuth2AuthorizationGrantAuthorization.ClaimsHolder value) {
        return this.serializer.serialize(value);
    }
}
