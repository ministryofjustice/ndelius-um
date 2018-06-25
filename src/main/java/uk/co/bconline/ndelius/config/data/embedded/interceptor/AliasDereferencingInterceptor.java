package uk.co.bconline.ndelius.config.data.embedded.interceptor;

import static java.util.Arrays.stream;

import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchEntry;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class AliasDereferencingInterceptor extends InMemoryOperationInterceptor
{
	private LDAPInterface server;

	@Override
	public void processSearchEntry(final InMemoryInterceptedSearchEntry entry)
	{
		val searchEntry = entry.getSearchEntry();
		val isAlias = stream(searchEntry.getAttributeValues("objectclass")).anyMatch("alias"::equalsIgnoreCase);

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
