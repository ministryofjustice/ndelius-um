package uk.co.bconline.ndelius.config.security.redis.convert;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@ReadingConverter
public class BytesToOAuth2AuthorizationRequestConverter implements Converter<byte[], OAuth2AuthorizationRequest> {

    private final JacksonJsonRedisSerializer<OAuth2AuthorizationRequest> serializer;

    public BytesToOAuth2AuthorizationRequestConverter() {
        this.serializer = new JacksonJsonRedisSerializer<>(SecurityJsonMapper.create(), OAuth2AuthorizationRequest.class);
    }

    @Override
    public OAuth2AuthorizationRequest convert(byte @NonNull [] value) {
        return this.serializer.deserialize(value);
    }
}
