package uk.co.bconline.ndelius.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange
{
	String message() default "Date must be between {min} and {max}";

	String min() default "1900-01-01";
	String max() default "2099-12-31";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
