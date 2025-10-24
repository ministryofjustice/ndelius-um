package uk.co.bconline.ndelius.transformer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.model.RoleGroup;
import uk.co.bconline.ndelius.model.entry.RoleGroupEntry;

@Component
public class RoleGroupTransformer {
    @Autowired
    private final RoleTransformer roleTransformer;

    public RoleGroupTransformer(RoleTransformer roleTransformer) {
        this.roleTransformer = roleTransformer;
    }

    public RoleGroup map(RoleGroupEntry group) {
        return RoleGroup.builder()
            .name(group.getName())
            .roles(roleTransformer.map(group.getRoles()))
            .build();
    }
}
