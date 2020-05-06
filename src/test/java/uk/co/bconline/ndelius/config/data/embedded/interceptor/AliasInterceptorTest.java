package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchEntry;
import com.unboundid.ldap.sdk.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AliasInterceptorTest
{
	private LDAPInterface server = mock(LDAPInterface.class);
	private AliasInterceptor interceptor = new AliasInterceptor();

	@Before
	public void setup()
	{
		interceptor.setServer(server);
	}

	@Test
	public void aliasesAreRetrieved() throws LDAPException
	{
		InMemoryInterceptedSearchEntry entry = mock(InMemoryInterceptedSearchEntry.class);
		when(entry.getRequest()).thenReturn(mock(ReadOnlySearchRequest.class));
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
		when(entry.getRequest()).thenReturn(mock(ReadOnlySearchRequest.class));
		when(entry.getSearchEntry()).thenReturn(new SearchResultEntry("cn=not-alias", new Attribute[]{
				new Attribute("objectclass", "not-alias")
		}));

		interceptor.processSearchEntry(entry);

		verify(entry, never()).setSearchEntry(any(SearchResultEntry.class));
	}
}