package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.TransactionGroup;

import java.util.Optional;

public interface RoleGroupService
{
    Iterable<TransactionGroup> getTransactionGroups();
    Optional<TransactionGroup> getTransactionGroup(String transactionGroupName);
}
