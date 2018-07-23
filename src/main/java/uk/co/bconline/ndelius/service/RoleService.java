package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.Transaction;
import uk.co.bconline.ndelius.model.ldap.OIDBusinessTransaction;

import java.util.List;

public interface RoleService
{
	Iterable<Transaction> getRoles();

    List<OIDBusinessTransaction> getTransactionsByParent(String parent, Class<?> parentClass);
}
