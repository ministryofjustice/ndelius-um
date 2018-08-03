package uk.co.bconline.ndelius.advice;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.model.ForbiddenResponse;
import uk.co.bconline.ndelius.service.RoleService;

@Slf4j(topic = "audit")
@Order(1)
@Aspect
@Component
public class AuthorisationHandler
{
	@Value("${authorisation.disabled:false}")
	private boolean disabled;

	private final RoleService service;

	@Autowired
	public AuthorisationHandler(RoleService service)
	{
		this.service = service;
	}

	@Around("@annotation(interaction)")
	public Object authorise(ProceedingJoinPoint joinPoint, Interaction interaction) throws Throwable
	{
		if (interaction.secured() && !disabled)
		{
			val username = ((UserDetails) getContext().getAuthentication().getPrincipal()).getUsername();
			val allowed = service.getUserInteractions(username).containsAll(asList(interaction.value()));
			if (!allowed)
			{
				if (interaction.audited()) log.error("{} {} {}", username, interaction.value(), joinPoint.getArgs());
				return new ResponseEntity<>(new ForbiddenResponse(username, interaction.value()), FORBIDDEN);
			}
		}
		return joinPoint.proceed();
	}
}
