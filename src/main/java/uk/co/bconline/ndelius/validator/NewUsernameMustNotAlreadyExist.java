package uk.co.bconline.ndelius.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@Constraint(validatedBy = NewUsernameMustNotAlreadyExistValidator.class)
public @interface NewUsernameMustNotAlreadyExist {
    String message() default "New username is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
