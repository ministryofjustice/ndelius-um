package uk.co.bconline.ndelius.service.impl;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.co.bconline.ndelius.model.OIDUser;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class OIDUserDetailsServiceTest
{
	@Autowired
	private OIDUserDetailsService service;

	@Test
	public void searchUsingInitial()
	{
		List<OIDUser> users = service.search("J Blog", 1, 10);

		assertFalse(users.isEmpty());
		users.forEach(user ->
				assertThat(user.getForenames(), startsWith("J"))
		);
	}
}