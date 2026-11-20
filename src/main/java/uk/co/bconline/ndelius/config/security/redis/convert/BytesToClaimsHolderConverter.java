package uk.co.bconline.ndelius.config.security.redis.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import uk.co.bconline.ndelius.config.security.redis.entity.OAuth2AuthorizationGrantAuthorization;

@ReadingConverter
public class BytesToClaimsHolderConverter implements Converter<byte[], OAuth2AuthorizationGrantAuthorization.ClaimsHolder> {

    private final JacksonJsonRedisSerializer<OAuth2AuthorizationGrantAuthorization.ClaimsHolder> serializer;

    public BytesToClaimsHolderConverter() {
        this.serializer = new JacksonJsonRedisSerializer<>(SecurityJsonMapper.forClaims(), OAuth2AuthorizationGrantAuthorization.ClaimsHolder.class);
    }

    @Override
    public OAuth2AuthorizationGrantAuthorization.ClaimsHolder convert(byte[] value) {
        return this.serializer.deserialize(value);
    }
}
