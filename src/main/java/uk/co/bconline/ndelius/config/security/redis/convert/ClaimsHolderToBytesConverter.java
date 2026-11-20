package uk.co.bconline.ndelius.config.security.redis.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2AuthorizationGrantAuthorization;

@WritingConverter
public class ClaimsHolderToBytesConverter implements Converter<OAuth2AuthorizationGrantAuthorization.ClaimsHolder, byte[]> {

    private final JacksonJsonRedisSerializer<OAuth2AuthorizationGrantAuthorization.ClaimsHolder> serializer;

    public ClaimsHolderToBytesConverter() {
        this.serializer = new JacksonJsonRedisSerializer<>(SecurityJsonMapper.forClaims(), OAuth2AuthorizationGrantAuthorization.ClaimsHolder.class);
    }

    @Override
    public byte[] convert(OAuth2AuthorizationGrantAuthorization.ClaimsHolder value) {
        return this.serializer.serialize(value);
    }
}
