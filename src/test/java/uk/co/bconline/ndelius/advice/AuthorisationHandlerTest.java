package uk.co.bconline.ndelius.advice;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.bconline.ndelius.advice.annotation.Interaction;
import uk.co.bconline.ndelius.service.impl.OIDUserDetailsService;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class AuthorisationHandlerTest
{
	private OIDUserDetailsService service = mock(OIDUserDetailsService.class);
	private ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
	private AuthorisationHandler handler = new AuthorisationHandler(service);

	@Test
	@WithMockUser
	public void authorisationFailsIfRoleIsMissing() throws Throwable
	{
		when(service.getUserRoles("user")).thenReturn(singletonList("ROLE_USER"));

		Object response = handler.authorise(joinPoint, interaction("SOME_OTHER_ROLE"));

		verify(joinPoint, never()).proceed();
		assertNotNull(response);
		assertTrue(response instanceof ResponseEntity);
		assertEquals(403, ((ResponseEntity) response).getStatusCodeValue());
	}

	@Test
	@WithMockUser
	public void authorisationSucceedsIfUserHasRole() throws Throwable
	{
		when(service.getUserRoles("user")).thenReturn(singletonList("ROLE_USER"));

		handler.authorise(joinPoint, interaction("ROLE_USER"));

		verify(joinPoint).proceed();
	}

	private Interaction interaction(String... value)
	{
		return new Interaction()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return Interaction.class;
			}

			@Override
			public String[] value()
			{
				return value;
			}

			@Override
			public boolean secured()
			{
				return true;
			}

			@Override
			public boolean audited()
			{
				return true;
			}
		};
	}
}