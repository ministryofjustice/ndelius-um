package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchEntry;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResultEntry;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

import static com.unboundid.ldap.sdk.SearchScope.SUB;
import static java.util.stream.Collectors.toList;

@Slf4j
@Configuration
public class MemberOfInterceptor extends InMemoryOperationInterceptor {
    @Value("${delius.ldap.base.groups}")
    private String groupsBase;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    private LDAPInterface server;

    @Override
    public void processSearchEntry(final InMemoryInterceptedSearchEntry entry) {
        if (!entry.getRequest().getAttributeList().contains("memberOf")) return;

        try {
            val searchEntry = entry.getSearchEntry();

            // fetch groups based on member attribute
            val baseDN = String.format("%s,%s", groupsBase, ldapBase);
            val result = server.search(baseDN, SUB, String.format("member=%s", searchEntry.getDN()), "dn");
            val groupDNs = result.getSearchEntries().stream().map(Entry::getDN).collect(toList());

            // add group DNs to the memberOf attribute
            val attrs = new HashSet<>(searchEntry.getAttributes());
            attrs.add(new Attribute("memberOf", groupDNs));

            // replace search result
            val newEntry = new SearchResultEntry(searchEntry.getMessageID(), searchEntry.getDN(),
                attrs, searchEntry.getControls());
            entry.setSearchEntry(newEntry);
        } catch (LDAPSearchException e) {
            log.error("Unable to dereference 'memberOf' attribute for entry {}", entry, e);
        }
    }

    public void setServer(LDAPInterface server) {
        this.server = server;
    }
}
