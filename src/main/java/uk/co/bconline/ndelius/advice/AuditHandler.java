package uk.co.bconline.ndelius.advice;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static uk.co.bconline.ndelius.util.AuthUtils.myUsername;

@Aspect
@Component
@Slf4j(topic = "audit")
public class AuditHandler
{
	@Around("@annotation(preauthorize)")
	public Object audit(ProceedingJoinPoint joinPoint, PreAuthorize preauthorize) throws Throwable
	{
		val matcher = Pattern.compile("#oauth2\\.hasScope\\('(.*)'\\)").matcher(preauthorize.value());
		if (matcher.find())
		{
			val interaction = matcher.group(1);
			log.info("{} {} {}", myUsername(), interaction, joinPoint.getArgs());
		}
		return joinPoint.proceed();
	}
}
