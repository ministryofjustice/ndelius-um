package uk.co.bconline.ndelius.advice;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
@ControllerAdvice
public class ValidationExceptionHandler
{
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map handle(MethodArgumentNotValidException exception) {
		return Collections.singletonMap("error", exception
				.getBindingResult()
				.getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(toList()));
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map handle(ConstraintViolationException exception) {
		return Collections.singletonMap("error", exception
				.getConstraintViolations().stream()
				.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
				.collect(toList()));
	}
}
