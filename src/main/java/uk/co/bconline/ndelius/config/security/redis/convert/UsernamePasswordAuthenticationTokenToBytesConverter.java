package uk.co.bconline.ndelius.config.security.redis.convert;

import org.jspecify.annotations.NonNull;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.jackson.SecurityJacksonModules;

@WritingConverter
public class UsernamePasswordAuthenticationTokenToBytesConverter implements Converter<UsernamePasswordAuthenticationToken, byte[]> {

    private final JacksonJsonRedisSerializer<UsernamePasswordAuthenticationToken> serializer;

    public UsernamePasswordAuthenticationTokenToBytesConverter() {
        ObjectMapper objectMapper = JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(BytesToUsernamePasswordAuthenticationTokenConverter.class.getClassLoader()))
            .build();
        this.serializer = new JacksonJsonRedisSerializer<>(objectMapper, UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public byte[] convert(@NonNull UsernamePasswordAuthenticationToken value) {
        return this.serializer.serialize(value);
    }
}
