package uk.co.bconline.ndelius.advice;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import uk.co.bconline.ndelius.util.AuthUtils;

import jakarta.servlet.http.HttpServletRequest;

import static java.util.Optional.ofNullable;
import static org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE;
import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Aspect
@Component
@Slf4j(topic = "audit")
public class AuditHandler {
	@EventListener
	public void authenticationFailure(AbstractAuthenticationFailureEvent event) {
		val username = ofNullable(event.getAuthentication()).map(Authentication::getPrincipal).orElse("UNKNOWN");
		val message = ofNullable(event.getException()).map(Exception::getMessage).orElse("Unknown error");
		log.error("{} {} {}", username, "AUTHENTICATION_FAILURE", message);
	}

	@EventListener
	public void authenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
		log.info("{} {}", myUsername(), "AUTHENTICATION_SUCCESS");
	}

	@Before("@annotation(preauthorize)")
	public void interactionSuccess(JoinPoint joinPoint, PreAuthorize preauthorize) {
		log.info("{} {} {}", myUsername(), AuthUtils.getRequiredScope(preauthorize), joinPoint.getArgs());
	}

	public String interactionFailure(HttpServletRequest request) {
		val requiredScope = ofNullable(request.getAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE))
				.map(HandlerMethod.class::cast)
				.flatMap(method -> ofNullable(method.getMethodAnnotation(PreAuthorize.class)))
				.map(AuthUtils::getRequiredScope)
				.orElse(null);
		log.error("{} {}", myUsername(), requiredScope);
		return requiredScope;
	}
}
