package uk.co.bconline.ndelius.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.bconline.ndelius.model.User;
import uk.co.bconline.ndelius.service.UserService;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class NewUsernameMustNotAlreadyExistValidator implements ConstraintValidator<NewUsernameMustNotAlreadyExist, Object[]> {
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(Object[] params, ConstraintValidatorContext context) {
        String newUsername = ((User) params[0]).getUsername();
        String oldUsername = (String) params[1];

        return oldUsername.equals(newUsername) || !userService.usernameExists(newUsername);
    }
}
