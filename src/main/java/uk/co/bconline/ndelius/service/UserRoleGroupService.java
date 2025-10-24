package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.RoleGroup;

import java.util.List;
import java.util.Optional;

public interface UserRoleGroupService {
    List<RoleGroup> getAssignableRoleGroups();

    Optional<RoleGroup> getRoleGroup(String transactionGroupName);
}
