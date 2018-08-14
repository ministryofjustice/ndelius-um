package uk.co.bconline.ndelius.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ValidDatesValidator.class)
public @interface ValidDates
{
	String message() default "Start Date must not be after End Date";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
