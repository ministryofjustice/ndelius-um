package uk.co.bconline.ndelius.config.security.redis.convert;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@WritingConverter
public class UsernamePasswordAuthenticationTokenToBytesConverter implements Converter<UsernamePasswordAuthenticationToken, byte[]> {

    private final JacksonJsonRedisSerializer<UsernamePasswordAuthenticationToken> serializer;

    public UsernamePasswordAuthenticationTokenToBytesConverter() {
        this.serializer = new JacksonJsonRedisSerializer<>(SecurityJsonMapper.create(), UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public byte[] convert(@NonNull UsernamePasswordAuthenticationToken value) {
        return this.serializer.serialize(value);
    }
}
