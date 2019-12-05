package uk.co.bconline.ndelius.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = PrefixMatchesProviderCodeValidator.class)
public @interface PrefixMatchesProviderCode
{
	String message() default "prefix should correspond to a valid provider code";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
