package uk.co.bconline.ndelius.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.model.Group;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserEntryService;
import uk.co.bconline.ndelius.transformer.GroupTransformer;

import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static uk.co.bconline.ndelius.util.AuthUtils.myInteractions;
import static uk.co.bconline.ndelius.util.Constants.PUBLIC_ACCESS;

@Slf4j
public class AssignableGroupsValidator implements ConstraintValidator<AssignableGroups, User> {
    @Autowired
    private UserEntryService userEntryService;

    @Autowired
    private GroupTransformer groupTransformer;

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        // Only users with Public RBAC Admin access are allowed to modify groups
        if (myInteractions().anyMatch(PUBLIC_ACCESS::equals)) return true;

        val username = ofNullable(user.getExistingUsername()).orElse(user.getUsername());
        val newGroups = ofNullable(user.getGroups()).orElse(emptyMap()).values().stream()
            .flatMap(List::stream)
            .sorted(comparing(Group::getName))
            .collect(toList());
        val existingGroups = groupTransformer.map(userEntryService.getUserGroups(username));

        // If the groups haven't changed, then this is valid
        return newGroups.equals(existingGroups);
    }
}
