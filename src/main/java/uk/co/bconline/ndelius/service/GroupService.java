package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.entry.GroupEntry;

import javax.naming.Name;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GroupService {
    Map<String, Set<GroupEntry>> getGroups();
    Set<GroupEntry> getGroups(String type);
    Set<GroupEntry> getGroups(Collection<Name> groupNames);
    Optional<GroupEntry> getGroup(String name);
    Optional<GroupEntry> getGroup(String type, String name);
}
