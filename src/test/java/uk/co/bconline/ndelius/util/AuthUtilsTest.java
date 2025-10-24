package uk.co.bconline.ndelius.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class AuthUtilsTest {
    @Before
    public void user() {
        SecurityContextHolder.getContext()
            .setAuthentication(new TestingAuthenticationToken("user", "password", "ROLE_USER"));
    }

    @Test
    public void returnsUserDetailsFromSecurityContext() {
        Authentication me = AuthUtils.me();
        assertEquals("user", me.getPrincipal());
        assertEquals("password", me.getCredentials());
    }

    @Test
    public void usernameIsReturned() {
        assertEquals("user", AuthUtils.myUsername());
    }

    @Test
    public void rolesAreReturned() {
        assertThat(AuthUtils.myInteractions().collect(toSet()), hasItem("ROLE_USER"));
    }

    @Test
    public void checkNationalAccessIsFalse() {
        assertFalse(AuthUtils.isNational());
    }

    @Test
    public void checkNationalAccessIsTrue() {
        SecurityContextHolder.getContext()
            .setAuthentication(new TestingAuthenticationToken("user", "password", "UABI025"));
        assertTrue(AuthUtils.isNational());
    }
}
