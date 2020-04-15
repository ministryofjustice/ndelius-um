package uk.co.bconline.ndelius.advice;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.co.bconline.ndelius.exception.AppException;
import uk.co.bconline.ndelius.model.ErrorResponse;
import uk.co.bconline.ndelius.model.ForbiddenResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.Iterator;

import static java.util.stream.Collectors.toList;
import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;
import static uk.co.bconline.ndelius.util.NameUtils.camelCaseToTitleCase;

@Slf4j
@Component
@ControllerAdvice
public class ControllerExceptionHandler
{
	private final AuditHandler auditHandler;

	public ControllerExceptionHandler(AuditHandler auditHandler) {
		this.auditHandler = auditHandler;
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handle(MethodArgumentNotValidException exception) {
		log.debug("Returning 400 response", exception);
		val result = exception.getBindingResult();
		val errors = result
				.getFieldErrors().stream()
				.map(e -> camelCaseToTitleCase(e.getField()) + " " + e.getDefaultMessage())
				.collect(toList());
		errors.addAll(result
				.getGlobalErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(toList()));
		return new ErrorResponse(errors);
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handle(ConstraintViolationException exception) {
		log.debug("Returning 400 response", exception);
		return new ErrorResponse(exception
				.getConstraintViolations().stream()
				.map(e -> {
					Path.Node node = getLast(e.getPropertyPath().iterator());
					if (node == null || node.getKind() != ElementKind.PROPERTY) {
						return e.getMessage();
					} else {
						return camelCaseToTitleCase(node.getName()) + " " + e.getMessage();
					}
				})
				.collect(toList()));
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ForbiddenResponse handle(AccessDeniedException exception, final HttpServletRequest request) {
		val requiredScope = auditHandler.interactionFailure(request);
		log.debug("Returning 403 response", exception);
		return new ForbiddenResponse(myUsername(), new String[]{ requiredScope });
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handle(AppException exception) {
		log.error("Returning 500 response", exception);
		if (exception.getMessage() == null) return null;
		return new ErrorResponse(exception.getMessage());
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handle(RuntimeException exception) {
		log.error("Returning 500 response", exception);
		return new ErrorResponse(getMostSpecificCause(exception).getMessage());
	}

	private <T> T getLast(Iterator<T> propertyPath) {
		T node = null;
		while (propertyPath.hasNext()) node = propertyPath.next();
		return node;
	}
}
