package uk.co.bconline.ndelius.transformer;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.Group;
import uk.co.bconline.ndelius.model.entry.GroupEntry;

import javax.naming.Name;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;

@Component
public class GroupTransformer {
    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${delius.ldap.base.groups}")
    private String groupsBase;

    public Group map(GroupEntry entry) {
        return Group.builder()
            .name(entry.getName())
            .type(entry.getType())
            .description(entry.getDescription())
            .build();
    }

    public Group mapWithMembers(GroupEntry entry) {
        return map(entry).toBuilder()
            .members(entry.getMembers().stream()
                .map(member -> LdapUtils.getStringValue(member, "cn"))
                .sorted()
                .collect(toList()))
            .build();
    }

    public Map<String, List<Group>> map(Map<String, Set<GroupEntry>> entryMap) {
        val builder = ImmutableMap.<String, List<Group>>builder();
        entryMap.forEach((key, value) -> builder.put(key, this.map(value)));
        return builder.build();
    }

    public List<Group> map(Collection<GroupEntry> entries) {
        return ofNullable(entries)
            .map(list -> list.stream()
                .map(this::map)
                .sorted(Comparator.comparing(Group::getName))
                .collect(toList()))
            .orElse(null);
    }

    public Map<String, List<Group>> groupedByType(Collection<GroupEntry> entries) {
        return ofNullable(entries)
            .map(list -> list.stream()
                .map(this::map)
                .sorted(Comparator.comparing(Group::getName))
                .collect(groupingBy(Group::getType)))
            .orElse(null);
    }

    public Set<Group> collate(Map<String, List<Group>> groups) {
        return ofNullable(groups)
            .map(list -> list.values().stream()
                .flatMap(List::stream)
                .collect(toSet()))
            .orElse(null);
    }

    public Set<Name> mapToNames(Collection<Group> groups) {
        return ofNullable(groups)
            .map(list -> list.stream()
                .map(group -> (Name) LdapNameBuilder.newInstance(ldapBase).add(groupsBase)
                    .add("ou", group.getType())
                    .add("cn", group.getName())
                    .build())
                .collect(toSet()))
            .orElse(null);
    }
}
