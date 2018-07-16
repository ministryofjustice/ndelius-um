package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.Transaction;

public interface RoleService
{
	Iterable<Transaction> getRoles();
}
