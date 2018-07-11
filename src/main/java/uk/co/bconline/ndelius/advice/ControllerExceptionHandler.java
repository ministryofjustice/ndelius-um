package uk.co.bconline.ndelius.advice;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

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

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.exception.AppException;

@Slf4j
@Component
@ControllerAdvice
public class ControllerExceptionHandler
{
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map handle(MethodArgumentNotValidException exception) {
		log.debug("Request validation failure. Returning 401 response", exception);
		return singletonMap("error", exception
				.getBindingResult()
				.getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(toList()));
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map handle(ConstraintViolationException exception) {
		log.debug("Request validation failure. Returning 401 response", exception);
		return singletonMap("error", exception
				.getConstraintViolations().stream()
				.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
				.collect(toList()));
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map handle(AppException exception) {
		log.error("Application exception occurred. Returning 500 response", exception);
		return singletonMap("error", exception.getMessage());
	}
}
