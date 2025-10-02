package uk.co.bconline.ndelius.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = AssignableDatasetsValidator.class)
public @interface AssignableDatasets
{
	String message() default "attempting to assign invalid datasets";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
