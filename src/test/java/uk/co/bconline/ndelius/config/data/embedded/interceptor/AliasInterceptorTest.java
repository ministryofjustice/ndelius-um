package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchEntry;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.ldap.sdk.SearchResultEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AliasInterceptorTest {
    private final LDAPInterface server = mock(LDAPInterface.class);
    private final AliasInterceptor interceptor = new AliasInterceptor();

    @BeforeEach
    public void setup() {
        interceptor.setServer(server);
    }

    @Test
    public void aliasesAreRetrieved() throws LDAPException {
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
        assertThat(captor.getValue().getDN()).isEqualTo("cn=different");
    }

    @Test
    public void nonAliasEntriesAreLeftAlone() {
        InMemoryInterceptedSearchEntry entry = mock(InMemoryInterceptedSearchEntry.class);
        when(entry.getRequest()).thenReturn(mock(ReadOnlySearchRequest.class));
        when(entry.getSearchEntry()).thenReturn(new SearchResultEntry("cn=not-alias", new Attribute[]{
            new Attribute("objectclass", "not-alias")
        }));

        interceptor.processSearchEntry(entry);

        verify(entry, never()).setSearchEntry(any(SearchResultEntry.class));
    }
}
