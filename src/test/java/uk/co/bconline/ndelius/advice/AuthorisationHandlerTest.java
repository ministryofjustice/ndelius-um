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
import uk.co.bconline.ndelius.model.ForbiddenResponse;
import uk.co.bconline.ndelius.service.RoleService;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class AuthorisationHandlerTest
{
	private RoleService service = mock(RoleService.class);
	private ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
	private AuthorisationHandler handler = new AuthorisationHandler(service);

	@Test
	@WithMockUser
	public void authorisationFailsIfRoleIsMissing() throws Throwable
	{
		when(service.getUserInteractions("user")).thenReturn(singletonList("ROLE_USER"));

		ResponseEntity response = (ResponseEntity) handler.authorise(joinPoint, interaction("SOME_OTHER_ROLE"));

		verify(joinPoint, never()).proceed();
		assertNotNull(response);
		assertEquals(403, response.getStatusCodeValue());
		assertTrue(response.getBody() instanceof ForbiddenResponse);
		ForbiddenResponse body = ((ForbiddenResponse) response.getBody());
		assertEquals("user", body.getUser());
		assertEquals(1, body.getRequiredRoles().length);
		assertEquals("SOME_OTHER_ROLE", body.getRequiredRoles()[0]);
	}

	@Test
	@WithMockUser
	public void authorisationSucceedsIfUserHasRole() throws Throwable
	{
		when(service.getUserInteractions("user")).thenReturn(singletonList("ROLE_USER"));

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