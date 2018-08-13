package uk.co.bconline.ndelius.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = MissingStaffCodeWithStaffGradeValidator.class)
public @interface MissingStaffCodeWithStaffGrade
{
	String message() default "Staff code is required when entering staff grade";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
