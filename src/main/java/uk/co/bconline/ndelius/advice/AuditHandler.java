package uk.co.bconline.ndelius.advice;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import uk.co.bconline.ndelius.advice.annotation.Interaction;

@Slf4j(topic = "audit")
@Order(2)
@Aspect
@Component
public class AuditHandler
{
	@Around("@annotation(interaction)")
	public Object audit(ProceedingJoinPoint joinPoint, Interaction interaction) throws Throwable
	{
		if (interaction.audited())
		{
			val user = (UserDetails) getContext().getAuthentication().getPrincipal();
			log.info("{} {} {}", user.getUsername(), interaction.value(), joinPoint.getArgs());
		}
		return joinPoint.proceed();
	}
}
