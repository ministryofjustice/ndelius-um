package uk.co.bconline.ndelius.advice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Interaction
{
	String[] value();
	boolean secured() default true;
	boolean audited() default true;
}
