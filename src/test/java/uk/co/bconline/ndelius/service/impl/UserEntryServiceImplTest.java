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
public class UserEntryServiceImplTest
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
	public void retrieveUserEntry()
	{
		service.getUser("test.user").ifPresent(entry -> {
			assertEquals("Test", entry.getForenames());
			assertEquals("User", entry.getSurname());
		});
	}
}