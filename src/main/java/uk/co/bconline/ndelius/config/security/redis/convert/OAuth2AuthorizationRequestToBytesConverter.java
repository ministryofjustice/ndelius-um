package uk.co.bconline.ndelius.config.security.redis.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@WritingConverter
public class OAuth2AuthorizationRequestToBytesConverter implements Converter<OAuth2AuthorizationRequest, byte[]> {

    private final JacksonJsonRedisSerializer<OAuth2AuthorizationRequest> serializer;

    public OAuth2AuthorizationRequestToBytesConverter() {
        this.serializer = new JacksonJsonRedisSerializer<>(SecurityJsonMapper.create(), OAuth2AuthorizationRequest.class);
    }

    @Override
    public byte[] convert(OAuth2AuthorizationRequest value) {
        return this.serializer.serialize(value);
    }
}
