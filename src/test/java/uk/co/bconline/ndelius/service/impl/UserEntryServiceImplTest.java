package uk.co.bconline.ndelius.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.service.UserRoleService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserEntryServiceImplTest {
    @Autowired
    private UserEntryServiceImpl service;

    @Autowired
    private UserRoleService roleService;

    @Test
    public void retrieveRoles() {
        Set<String> roles = roleService.getUserInteractions("test.user");

        assertThat(roles)
                .isNotEmpty()
                .contains("UMBI001")
                .doesNotContain("UMBI999");
    }

    @Test
    public void retrieveUserEntry() {
        assertThat(service.getUser("test.user"))
                .isPresent()
                .get()
                .satisfies(entry -> {
                    assertThat(entry.getForenames()).isEqualTo("Test");
                    assertThat(entry.getSurname()).isEqualTo("User");
                });
    }

    @Test
    public void searchWithDatasetPasses() {
        List<SearchResult> results = service.search("test.user", false, Set.of("N01"));

        assertThat(results)
                .isNotEmpty()
                .anySatisfy(user -> assertThat(user.getUsername()).isEqualTo("test.user"));
    }

    @Test
    public void searchReturnsInactiveUser() {
        List<SearchResult> results = service.search("test.user.inactive", true, Collections.emptySet());

        assertThat(results)
                .isNotEmpty()
                .anySatisfy(user -> assertThat(user.getUsername()).isEqualTo("test.user.inactive"));
    }

    @Test
    public void searchByEmailPasses() {
        List<SearchResult> results = service.search("test.user@test.com", false, Collections.emptySet());

        assertThat(results)
                .isNotEmpty()
                .anySatisfy(user -> assertThat(user.getEmail()).isEqualTo("test.user@test.com"));
    }

    @Test
    public void searchEmailWithoutAtSymbolFails() {
        List<SearchResult> results = service.search("test.com", false, Collections.emptySet());

        assertThat(results).isEmpty();
    }

    @Test
    public void verifyEmailSearchUsesLDAPSource() {
        List<SearchResult> results = service.search("@test.com", false, Collections.emptySet());
        assertThat(results).allSatisfy(result -> assertThat(result.getSources()).contains("LDAP"));
    }
}
