package uk.co.bconline.ndelius.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerGetUserTest {
	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setup() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.alwaysDo(print())
				.build();
	}

	@Test
	public void combinedUserIsReturned() throws Exception {
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", equalTo("test.user")))
				.andExpect(jsonPath("$.forenames", equalTo("Test")))		// From LDAP
				.andExpect(jsonPath("$.surname", equalTo("User")))			// From LDAP
				.andExpect(jsonPath("$.staffCode", equalTo("N01A001")))		// From DB
				.andExpect(jsonPath("$.teams[*].code", hasItems("N01TST", "N02TST", "N03TST")));
	}

	@Test
	public void nonExistentUserReturns404() throws Exception {
		mvc.perform(get("/api/user/nonexistent-user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isNotFound());
	}

	@Test
	public void usersAreFilteredOnDatasets() throws Exception {
		// Attempt to lookup N02 user, as a non-national N01 user
		mvc.perform(get("/api/user/Joe.Bloggs")
				.header("Authorization", "Bearer " + token(mvc, "test.user.local")))
				.andExpect(status().isNotFound());
	}

	@Test
	public void emailAddressIsReturned() throws Exception {
		String token = token(mvc);

		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("test.user@test.com")));
	}

	@Test
	public void telephoneNumberIsReturned() throws Exception {
		String token = token(mvc);

		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.telephoneNumber", is("0123 456 789")));
	}

	@Test
	public void subContractedProviderIsReturned() throws Exception {
		String token = token(mvc);

		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.subContractedProvider.code", is("N01SC1")));
	}

	@Test
	public void userRolesHaveDescriptionsRegardlessOfAdminAccess() throws Exception {
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andDo(mvcResult -> {
					String json = mvcResult.getResponse().getContentAsString();
					List<String> descriptions = JsonPath.parse(json).read("$.roles[*].description");
					List<String> names = JsonPath.parse(json).read("$.roles[*].name");
					assertThat(descriptions, hasSize(names.size()));
				});
	}

	@Test
	public void handlingNullCreatedUpdatedDetails() throws Exception {
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.created").doesNotExist())
				.andExpect(jsonPath("$.updated").doesNotExist());
	}

	@Test
	public void oracleStartAndEndDatesAreReturnedByDefault() throws Exception {
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.startDate", is("2000-01-01")));
	}

	@Test
	public void groupsAreReturned() throws Exception {
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.groups.Fileshare", hasSize(1)))
				.andExpect(jsonPath("$.groups.NDMIS-Reporting", hasSize(2)));
	}
}
