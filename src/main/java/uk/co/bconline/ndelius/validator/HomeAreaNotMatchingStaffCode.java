package uk.co.bconline.ndelius.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = HomeAreaNotMatchingStaffCodeValidator.class)
public @interface HomeAreaNotMatchingStaffCode
{
	String message() default "Home area code doesn't match staff code prefix";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
