package uk.co.bconline.ndelius.service;

import uk.co.bconline.ndelius.model.entry.GroupEntry;

import javax.naming.Name;
import java.util.Collection;
import java.util.Set;

public interface GroupService {
    Set<GroupEntry> getGroups();
    Set<GroupEntry> getGroups(Collection<Name> groupNames);
}
