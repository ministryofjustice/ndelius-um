package uk.co.bconline.ndelius.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.bconline.ndelius.service.UserRoleService;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class OIDUserDetailsServiceTest
{
	@Autowired
	private UserEntryServiceImpl service;

	@Autowired
	private UserRoleService roleService;

	@Test
	public void retrieveRoles()
	{
		Set<String> roles = roleService.getUserInteractions("test.user");

		assertFalse(roles.isEmpty());
		assertThat(roles, hasItem("UMBI001"));
		assertThat(roles, not(hasItem("UMBI999")));
	}

	@Test
	public void retrieveOIDUser()
	{
		service.getUser("test.user").ifPresent(oidUser -> {
			assertEquals("Test", oidUser.getForenames());
			assertEquals("User", oidUser.getSurname());
		});
	}
}