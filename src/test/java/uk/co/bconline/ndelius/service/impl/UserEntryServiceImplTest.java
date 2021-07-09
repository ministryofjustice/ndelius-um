package uk.co.bconline.ndelius.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.LdapDataEntry;
import org.springframework.ldap.NoSuchAttributeException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.NameAwareAttributes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.bconline.ndelius.model.entry.UserEntry;
import uk.co.bconline.ndelius.model.entry.UserPreferencesEntry;
import uk.co.bconline.ndelius.repository.ldap.UserEntryRepository;
import uk.co.bconline.ndelius.repository.ldap.UserPreferencesRepository;
import uk.co.bconline.ndelius.service.GroupService;
import uk.co.bconline.ndelius.service.UserRoleService;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.ldap.support.LdapUtils.newLdapName;

@RunWith(SpringRunner.class)
public class UserEntryServiceImplTest {
	@Mock
	private DirContext context;

	@Mock
	private ContextSource contextSource;

	@Mock
	private LdapTemplate ldapTemplate;

	@Mock
	private GroupService groupService;

	@Mock
	private UserPreferencesRepository userPreferencesRepository;

	@Mock
	private UserRoleService userRoleService;

	@Mock
	private UserEntryRepository userEntryRepository;

	@InjectMocks
	private UserEntryServiceImpl service;

	@Test
	public void rebuildUserOnEndDateException() throws Exception {
		// Given an existing user with a preferences entry
		ReflectionTestUtils.setField(service, "ldapBase", "dc=example");
		// - user
		Name dn = newLdapName("cn=test.user,dc=example");
		UserEntry user = UserEntry.builder().dn(dn).forenames("test").build();
		LdapDataEntry userResult = new DirContextAdapter(new BasicAttributes("givenName", "test"), dn);
		// - preferences
		Name prefDn = newLdapName("cn=UserPreferences,cn=test.user,dc=example");
		UserPreferencesEntry pref = new UserPreferencesEntry("test.user");
		SearchResult prefResult = new SearchResult(prefDn.toString(), pref, new BasicAttributes("prefKey", "prefVal"));
		prefResult.setNameInNamespace(prefDn.toString());
		// - mock methods
		when(contextSource.getReadOnlyContext()).thenReturn(context);
		when(ldapTemplate.getContextSource()).thenReturn(contextSource);
		when(ldapTemplate.lookup(dn)).thenReturn(userResult);
		when(context.search(dn, null)).thenReturn(new TestNamingEnumeration<>(prefResult));

		// And the update will throw an error
		when(userEntryRepository.save(user))
				// Fail the first time
				.thenThrow(new NoSuchAttributeException("[LDAP: error code 16 - modify/delete: endDate: no such attribute]"))
				// Success on retry
				.thenReturn(user);

		// When I attempt to save changes
		service.save(user);

		// Then the user and preferences are rebuilt
		verify(ldapTemplate, times(1)).unbind(dn, true);
		verify(ldapTemplate, times(1)).bind(dn, userResult, new NameAwareAttributes(new BasicAttributes("givenName", "test")));
		verify(ldapTemplate, times(1)).bind(newLdapName("cn=UserPreferences,cn=test.user"), pref, new BasicAttributes("prefKey", "prefVal"));

		// And the update is retried
		verify(userEntryRepository, times(2)).save(user);
	}

	static class TestNamingEnumeration<T> implements NamingEnumeration<T> {
		private final Iterator<T> iterator;
		@SafeVarargs TestNamingEnumeration(T... values) { this.iterator = List.of(values).iterator(); }
		public T next() { return iterator.next(); }
		public boolean hasMore() { return iterator.hasNext(); }
		public void close() throws NamingException { }
		public boolean hasMoreElements() { return hasMore(); }
		public T nextElement() { return next(); }
	}
}