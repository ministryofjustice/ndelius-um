package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.test.context.ActiveProfiles;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.repository.ldap.UserEntryRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MemberOfInterceptorTest {

    @Autowired
    private UserEntryRepository userEntryRepository;

    @Test
    public void memberOfAttributeIsPopulatedDynamicallyBasedOnGroupMembers() {
        Optional<UserEntry> user = userEntryRepository.findByUsername("test.user");
        assertThat(user).isPresent();
        assertThat(user.orElseThrow().getGroupNames())
                .hasSize(3)
                .contains(
            LdapUtils.newLdapName("cn=Group 1,ou=NDMIS-Reporting,ou=Groups,dc=bcl,dc=local"),
            LdapUtils.newLdapName("cn=Group 2,ou=NDMIS-Reporting,ou=Groups,dc=bcl,dc=local"),
                        LdapUtils.newLdapName("cn=Group 1,ou=Fileshare,ou=Groups,dc=bcl,dc=local"));
    }
}
