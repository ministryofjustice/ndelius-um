package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.TransactionGroup;

public interface RoleGroupService
{
    Iterable<TransactionGroup> getTransactionGroups();
}
