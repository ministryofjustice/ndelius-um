package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.RoleGroup;

public interface RoleGroupService
{
    List<RoleGroup> getRoleGroups();
    Optional<RoleGroup> getRoleGroup(String transactionGroupName);
}
