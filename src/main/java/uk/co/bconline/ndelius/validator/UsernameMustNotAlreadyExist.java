package uk.co.bconline.ndelius.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@Constraint(validatedBy = UsernameMustNotAlreadyExistValidator.class)
public @interface UsernameMustNotAlreadyExist {
	String message() default "Username is already in use";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
