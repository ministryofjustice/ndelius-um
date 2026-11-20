package uk.co.bconline.ndelius.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void oidPasswordFormatCanBeMatched() {
        String oidPassword = toOidPassword(new LdapShaPasswordEncoder().encode("secret"));

        boolean match = passwordEncoder.matches("secret", oidPassword);

        assertThat(match).isTrue();
    }

    @Test
    public void normalPasswordFormatCanBeMatched() {
        String encodedPassword = new LdapShaPasswordEncoder().encode("secret");

        boolean match = passwordEncoder.matches("secret", encodedPassword);

        assertThat(match).isTrue();
    }

    private static String toOidPassword(String encodedPassword) {
        byte[] bytes = encodedPassword.getBytes(StandardCharsets.UTF_8);
        return Arrays.toString(bytes).replace("[", "").replace("]", "").replace(" ", "");
    }
}
