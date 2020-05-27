package uk.co.bconline.ndelius.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.ldap.query.SearchScope;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.bconline.ndelius.model.*;
import uk.co.bconline.ndelius.model.entry.UserPreferencesEntry;
import uk.co.bconline.ndelius.repository.ldap.UserPreferencesRepository;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.CustomMatchers.isWithin;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;
import static uk.co.bconline.ndelius.test.util.UserUtils.aValidUser;
import static uk.co.bconline.ndelius.test.util.UserUtils.nextTestUsername;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerCreateTest
{
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserPreferencesRepository preferencesRepository;

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
	public void addUserWithNoUsername() throws Exception
	{
		String token = token(mvc);
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(null)
						.build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("Username must not be blank")));
	}

	@Test
	public void addUserWithExistingUsername() throws Exception
	{
		String token = token(mvc);
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username("test.user")
						.build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("Username is already in use")));
	}

	@Test
	public void userDetailsArePersistedCorrectlyOnCreation() throws Exception
	{
		String username = nextTestUsername();
		String token = token(mvc);
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username)
						.email("test1@test.com")
						.forenames("Test")
						.surname("User1")
						.staffCode("N01A999")
						.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
						.privateSector(false)
						.homeArea(Dataset.builder().code("N01").build())
						.startDate(LocalDate.of(2000, 1, 1))
						.endDate(LocalDate.of(2001, 2, 2))
						.teams(singletonList(Team.builder()
								.code("N01TST")
								.build()))
						.datasets(asList(
								Dataset.builder().code("N01").build(),
								Dataset.builder().code("N02").build()))
						.roles(singletonList(Role.builder()
								.name("UMBT001")
								.build()))
						.groups(ImmutableMap.of(
								"Fileshare", singletonList(Group.builder().name("Group 1").type("Fileshare").build()),
								"NDMIS-Reporting", singletonList(Group.builder().name("Group 2").type("NDMIS-Reporting").build())
						))
						.build())))
				.andExpect(status().isCreated())
				.andExpect(redirectedUrl("/user/" + username));

		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(username)))
				.andExpect(jsonPath("$.email", is("test1@test.com")))
				.andExpect(jsonPath("$.staffCode", is("N01A999")))
				.andExpect(jsonPath("$.staffGrade.code", is("GRADE2")))
				.andExpect(jsonPath("$.startDate", is("2000-01-01")))
				.andExpect(jsonPath("$.datasets[*].code", hasItems("N01", "N02")))
				.andExpect(jsonPath("$.teams[0].code", is("N01TST")))
				.andExpect(jsonPath("$.homeArea.code", is("N01")))
				.andExpect(jsonPath("$.roles", hasSize(1)))
				.andExpect(jsonPath("$.roles[0].name", is("UMBT001")))
				.andExpect(jsonPath("$.roles[0].interactions", hasItem("UMBI001")))
				.andExpect(jsonPath("$.groups[*][*].name", hasItems("Group 1", "Group 2")))
				.andExpect(jsonPath("$.created.user.username", is("test.user")))
				.andExpect(jsonPath("$.created.time", isWithin(5, SECONDS).of(now())))
				.andExpect(jsonPath("$.updated.user.username", is("test.user")))
				.andExpect(jsonPath("$.updated.time", isWithin(5, SECONDS).of(now())));
	}

	@Test
	public void usersAreCreatedWithDefaultPreferences() throws Exception
	{
		String username = nextTestUsername();
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token(mvc))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username)
						.build())))
				.andExpect(status().isCreated());

		Optional<UserPreferencesEntry> prefs = preferencesRepository.findOne(query()
				.searchScope(SearchScope.ONELEVEL)
				.base("cn=" + username + ",ou=Users")
				.where("cn").is("UserPreferences"));
		assertTrue(prefs.isPresent());
		assertEquals("NRO16", prefs.get().getMostRecentlyViewedOffenders());
	}

	@Test
	public void staffCodePrefixMustBeAValidProviderCode() throws Exception
	{
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token(mvc))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(nextTestUsername())
						.staffCode("ZZZA001").build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("Staff Code prefix should correspond to a valid provider code")));
	}

	@Test
	public void groupsWithNoNameAreRejected() throws Exception
	{
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token(mvc))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(nextTestUsername())
						.groups(ImmutableMap.of(
								"Fileshare", singletonList(Group.builder().build())
						)).build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("Name must not be blank")));
	}

	@Test
	public void groupsWithNoTypeAreRejected() throws Exception
	{
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token(mvc))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(nextTestUsername())
						.groups(ImmutableMap.of(
								"Fileshare", singletonList(Group.builder().name("NO-TYPE!").build())
						)).build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("Type must not be blank")));
	}

	@Test
	public void rolesWithNoNameAreRejected() throws Exception
	{
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token(mvc))
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(nextTestUsername())
						.roles(singletonList(Role.builder().build()))
						.build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("Name must not be blank")));
	}

	@Test
	public void groupsCannotBeAssignedByNonMembers() throws Exception {
		// Given I login as a local (non-national) admin, who is a member of Fileshare Group 1 only
		String token = token(mvc, "test.user.local");

		// When I attempt to assign a group that I am not a member of
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(nextTestUsername())
						.groups(ImmutableMap.of(
								"Fileshare", singletonList(Group.builder().name("Group 2").type("Fileshare").build())
						))
						.build())))

		// Then the request should be rejected
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("attempting to assign invalid groups")));
	}


	@Test
	public void groupsCanBeAssignedByMembers() throws Exception {
		// Given I login as a local (non-national) admin, who is a member of Fileshare Group 1 only
		String token = token(mvc, "test.user.local");

		// When I attempt to assign a group that I am a member of
		String username = nextTestUsername();
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username)
						.groups(ImmutableMap.of(
								"Fileshare", singletonList(Group.builder().name("Group 1").type("Fileshare").build())
						))
						.build())))
				.andExpect(status().isCreated());


		// Then the user should be created successfully with the group
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.groups[*][*].name", hasItem("Group 1")))
				.andExpect(jsonPath("$.groups[*][*].type", hasItem("Fileshare")));
	}
}
