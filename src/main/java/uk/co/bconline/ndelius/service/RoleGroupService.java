package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.RoleGroup;

import java.util.List;
import java.util.Optional;

public interface RoleGroupService {
    List<RoleGroup> getRoleGroups();

    Optional<RoleGroup> getRoleGroup(String transactionGroupName);
}
