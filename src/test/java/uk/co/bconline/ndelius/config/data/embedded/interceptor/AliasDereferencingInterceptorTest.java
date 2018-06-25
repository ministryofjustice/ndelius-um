package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchEntry;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchResultEntry;

public class AliasDereferencingInterceptorTest
{
	private LDAPInterface server = mock(LDAPInterface.class);
	private AliasDereferencingInterceptor interceptor = new AliasDereferencingInterceptor();

	@Before
	public void setup()
	{
		interceptor.setServer(server);
	}

	@Test
	public void aliasesAreRetrieved() throws LDAPException
	{
		InMemoryInterceptedSearchEntry entry = mock(InMemoryInterceptedSearchEntry.class);
		when(entry.getSearchEntry()).thenReturn(new SearchResultEntry("cn=alias", new Attribute[]{
				new Attribute("objectclass", "alias"),
				new Attribute("aliasedObjectName", "cn=different")
		}));
		when(server.getEntry("cn=different")).thenReturn(new SearchResultEntry("cn=different", new Attribute[0]));

		interceptor.processSearchEntry(entry);

		ArgumentCaptor<SearchResultEntry> captor = ArgumentCaptor.forClass(SearchResultEntry.class);
		verify(entry).setSearchEntry(captor.capture());
		assertEquals("cn=different", captor.getValue().getDN());
	}

	@Test
	public void nonAliasEntriesAreLeftAlone()
	{
		InMemoryInterceptedSearchEntry entry = mock(InMemoryInterceptedSearchEntry.class);
		when(entry.getSearchEntry()).thenReturn(new SearchResultEntry("cn=not-alias", new Attribute[]{
				new Attribute("objectclass", "not-alias")
		}));

		interceptor.processSearchEntry(entry);

		verify(entry, never()).setSearchEntry(any(SearchResultEntry.class));
	}
}