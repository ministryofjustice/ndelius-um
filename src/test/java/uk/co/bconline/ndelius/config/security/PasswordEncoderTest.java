package uk.co.bconline.ndelius.config.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class PasswordEncoderTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void oidPasswordFormatCanBeMatched() {
        byte[] bytes = new LdapShaPasswordEncoder().encode("secret").getBytes();
        String oidPassword = Arrays.toString(bytes).replace("[", "").replace("]", "").replaceAll(" ", "");

        boolean match = passwordEncoder.matches("secret", oidPassword);

        assertTrue(match);
    }

    @Test
    public void normalPasswordFormatCanBeMatched() {
        String encodedPassword = new LdapShaPasswordEncoder().encode("secret");

        boolean match = passwordEncoder.matches("secret", encodedPassword);

        assertTrue(match);
    }
}
