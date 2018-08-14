package uk.co.bconline.ndelius.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(ConditionallyRequired.List.class)
@Constraint(validatedBy = ConditionallyRequiredValidator.class)
public @interface ConditionallyRequired
{
	String required();
	String ifPopulated();

	String message() default "";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	@Target(TYPE)
	@Retention(RUNTIME)
	@interface List {
		ConditionallyRequired[] value();
	}
}
