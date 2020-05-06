package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.repository.ldap.UserEntryRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsIterableContaining.hasItems;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class MemberOfInterceptorTest {

	@Autowired
	private UserEntryRepository userEntryRepository;

	@Test
	public void memberOfAttributeIsPopulatedDynamicallyBasedOnGroupMembers() {
		Optional<UserEntry> user = userEntryRepository.findByUsername("test.user");
		assertTrue(user.isPresent());
		assertThat(user.get().getGroupNames(), hasSize(3));
		assertThat(user.get().getGroupNames(), hasItems(
				LdapUtils.newLdapName("cn=Group 1,ou=NDMIS-Reporting,ou=Groups,dc=bcl,dc=local"),
				LdapUtils.newLdapName("cn=Group 2,ou=NDMIS-Reporting,ou=Groups,dc=bcl,dc=local"),
				LdapUtils.newLdapName("cn=Group 1,ou=Fileshare,ou=Groups,dc=bcl,dc=local")));
	}
}