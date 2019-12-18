package uk.co.bconline.ndelius.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class RoleControllerTest
{
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup()
	{
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.alwaysDo(print())
				.build();
	}

	@Test
	public void rolesAreReturned() throws Exception
	{
		mvc.perform(get("/api/roles")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andExpect(jsonPath("$[*].name", hasItem("UMBT001")))
				.andExpect(jsonPath("$[*].name", hasItem("UMBT002")));
	}

	@Test
	public void rolesAreFilteredBasedOnAttributes() throws Exception
	{
		// UABT001 has adminlevel set to local, test.user is not allowed access to local-level roles so should't see it
		mvc.perform(get("/api/roles")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].name", not(hasItem("UABT001"))));
	}
}