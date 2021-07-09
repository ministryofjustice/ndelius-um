package uk.co.bconline.ndelius.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.bconline.ndelius.model.SearchResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserEntryServiceIntegrationTest {

	@Autowired
	private UserEntryService service;

	@Test
	public void retrieveUserEntry() {
		service.getUser("test.user").ifPresent(entry -> {
			assertEquals("Test", entry.getForenames());
			assertEquals("User", entry.getSurname());
		});
	}

	@Test
	public void searchWithDatasetPasses() {
		List<SearchResult> results = service.search("test.user", false, Set.of("N01"));

		assertFalse(results.isEmpty());
		assertTrue(results.stream().anyMatch((user) -> user.getUsername().equals("test.user")));
	}

	@Test
	public void searchReturnsInactiveUser() {
		List<SearchResult> results = service.search("test.user.inactive", true, Collections.emptySet());

		assertFalse(results.isEmpty());
		assertTrue(results.stream().anyMatch((user) -> user.getUsername().equals("test.user.inactive")));
	}

	@Test
	public void searchByEmailPasses() {
		List<SearchResult> results = service.search("test.user@test.com", false, Collections.emptySet());

		assertFalse(results.isEmpty());
		assertTrue(results.stream().anyMatch((user) -> user.getEmail().equals("test.user@test.com")));
	}

	@Test
	public void searchEmailWithoutAtSymbolFails() {
		List<SearchResult> results = service.search("test.com", false, Collections.emptySet());

		assertTrue(results.isEmpty());
	}

	@Test
	public void verifyEmailSearchUsesLDAPSource() {
		List<SearchResult> results = service.search("@test.com", false, Collections.emptySet());
		assertTrue(results.stream().allMatch(r -> r.getSources().contains("LDAP")));
	}
}
