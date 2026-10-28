package uk.co.bconline.ndelius.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthUtilsTest {
    @BeforeEach
    public void user() {
        SecurityContextHolder.getContext()
            .setAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"));
    }

    @Test
    public void returnsUserDetailsFromSecurityContext() {
        Authentication me = AuthUtils.me();
        assertThat(me.getPrincipal()).isEqualTo("user");
        assertThat(me.getCredentials()).isEqualTo("password");
    }

    @Test
    public void usernameIsReturned() {
        assertThat(AuthUtils.myUsername()).isEqualTo("user");
    }

    @Test
    public void rolesAreReturned() {
        assertThat(AuthUtils.myInteractions().collect(toSet())).contains("ROLE_USER");
    }

    @Test
    public void checkNationalAccessIsFalse() {
        assertThat(AuthUtils.isNational()).isFalse();
    }

    @Test
    public void checkNationalAccessIsTrue() {
        SecurityContextHolder.getContext()
            .setAuthentication(new TestingAuthenticationToken("user", "password", "UABI025"));
        assertThat(AuthUtils.isNational()).isTrue();
    }
}
