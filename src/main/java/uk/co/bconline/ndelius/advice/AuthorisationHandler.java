package uk.co.bconline.ndelius.advice;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.ForbiddenResponse;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Slf4j(topic = "audit")
@Order(1)
@Aspect
@Component
public class AuthorisationHandler
{
	@Around("@annotation(interaction)")
	public Object authorise(ProceedingJoinPoint joinPoint, Interaction interaction) throws Throwable
	{
		val t = now();
		if (interaction.secured())
		{
			val user = (UserDetails) getContext().getAuthentication().getPrincipal();
			val username = user.getUsername();
			val authorities = user.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.collect(toList());

			if (!authorities.containsAll(asList(interaction.value())))
			{
				if (interaction.audited()) log.error("{} {} {}", username, interaction.value(), joinPoint.getArgs());
				return new ResponseEntity<>(new ForbiddenResponse(username, interaction.value()), FORBIDDEN);
			}
		}
		log.trace("--{}ms	Authorisation", MILLIS.between(t, LocalDateTime.now()));
		return joinPoint.proceed();
	}
}
