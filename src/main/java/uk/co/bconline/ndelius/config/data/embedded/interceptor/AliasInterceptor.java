package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchEntry;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Configuration;

import static java.util.Arrays.stream;
import static uk.co.bconline.ndelius.util.LdapUtils.OBJECTCLASS;

@Slf4j
@Configuration
public class AliasInterceptor extends InMemoryOperationInterceptor
{
	private LDAPInterface server;

	@Override
	public void processSearchEntry(final InMemoryInterceptedSearchEntry entry)
	{
		if (entry.getRequest().getDereferencePolicy() == DereferencePolicy.NEVER) return;

		val searchEntry = entry.getSearchEntry();
		val isAlias = stream(searchEntry.getAttributeValues(OBJECTCLASS)).anyMatch("alias"::equalsIgnoreCase);

		if(isAlias)
		{
			try
			{
				val aliasedObjectName = searchEntry.getAttributeValue("aliasedobjectname");
				entry.setSearchEntry(server.getEntry(aliasedObjectName));
			}
			catch (LDAPException e)
			{
				log.error("Unable to dereference entry {}", entry, e);
			}
		}
	}

	public void setServer(LDAPInterface server)
	{
		this.server = server;
	}
}
