package uk.co.bconline.ndelius.advice;

import static java.util.stream.Collectors.toList;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.exception.NotFoundException;
import uk.co.bconline.ndelius.model.ErrorResponse;

@Slf4j
@Component
@ControllerAdvice
public class ControllerExceptionHandler
{
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handle(MethodArgumentNotValidException exception) {
		log.debug("Returning 401 response", exception);
		return new ErrorResponse(exception
				.getBindingResult()
				.getFieldErrors().stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage())
				.collect(toList()));
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handle(ConstraintViolationException exception) {
		log.debug("Returning 401 response", exception);
		return new ErrorResponse(exception
				.getConstraintViolations().stream()
				.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
				.collect(toList()));
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handle(NotFoundException exception) {
		log.debug("Returning 404 response", exception);
		if (exception.getMessage() == null) return null;
		return new ErrorResponse(exception.getMessage());
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handle(AppException exception) {
		log.error("Returning 500 response", exception);
		if (exception.getMessage() == null) return null;
		return new ErrorResponse(exception.getMessage());
	}
}
