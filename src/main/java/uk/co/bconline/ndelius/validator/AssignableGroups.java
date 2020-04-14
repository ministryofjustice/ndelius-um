package uk.co.bconline.ndelius.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = AssignableGroupsValidator.class)
public @interface AssignableGroups
{
	String message() default "attempting to assign invalid groups";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
