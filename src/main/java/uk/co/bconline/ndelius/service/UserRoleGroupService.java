package uk.co.bconline.ndelius.service;

import java.util.List;
import java.util.Optional;

import uk.co.bconline.ndelius.model.RoleGroup;

public interface UserRoleGroupService
{
    List<RoleGroup> getAssignableRoleGroups();
    Optional<RoleGroup> getRoleGroup(String transactionGroupName);
}
