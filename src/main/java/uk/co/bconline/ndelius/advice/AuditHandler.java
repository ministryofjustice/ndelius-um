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

import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
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
		val matcher = Pattern.compile("#oauth2\\.hasScope\\('(.*)'\\)").matcher(preauthorize.value());
		if (matcher.find())
		{
			val interaction = matcher.group(1);
			log.info("{} {} {}", myUsername(), interaction, joinPoint.getArgs());
		}
	}
}
