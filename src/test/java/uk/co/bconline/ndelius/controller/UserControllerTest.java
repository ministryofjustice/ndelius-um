package uk.co.bconline.ndelius.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
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
public class UserControllerTest
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
	public void searchQueryIsRequired() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void maxPageSizeForSearchIs100() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "")
				.param("pageSize", "101"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void minPageSizeIs1() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "")
				.param("pageSize", "0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void searchOnName() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "j blog"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].forenames", startsWith("J")))
				.andExpect(jsonPath("$[1].forenames", startsWith("J")));
	}

	@Test
	public void searchOnUsername() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "joe.bloggs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andExpect(jsonPath("$[0].username", is("Joe.Bloggs")));
	}

	@Test
	public void searchOnTeamDescriptionReturnsWholeTeam() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test team"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andExpect(jsonPath("$[*].username", hasItems("test.user", "Joe.Bloggs", "Jane.Bloggs")));
	}

	@Test
	public void searchOnStaffCode() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "N01A001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andExpect(jsonPath("$[0].staffCode", is("N01A001")));
	}

	@Test
	public void emptyQueryReturnsNoResult() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", ""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void searchResultsAreLimitedToPageSize() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "j blog")
				.param("pageSize", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void searchResultsAreFilteredOnTheCurrentUsersDatasets() throws Exception
	{
		// Given I login as N01 user
		String token = token(mvc, "test.user.local");

		// When I search for an N04 user, Then I should get no results
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token)
				.param("q", "Jim.Bloggs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", not(hasItem("Jim.Bloggs"))));

		// When I search for an N01 user, Then I should get results
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token)
				.param("q", "Tiffiny.Thrasher"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", hasItem("Tiffiny.Thrasher")));
	}

	@Test
	public void combinedUserIsReturned() throws Exception
	{
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
	public void combinedUserIsReturnedInSearchResults() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test.user"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username", equalTo("test.user")))
				.andExpect(jsonPath("$[0].forenames", equalTo("Test")))
				.andExpect(jsonPath("$[0].surname", equalTo("User")))
				.andExpect(jsonPath("$[0].staffCode", equalTo("N01A001")))
				.andExpect(jsonPath("$[0].teams[*].code", hasItems("N01TST", "N02TST", "N03TST")));
	}

	@Test
	public void combinedUserIsReturnedInSearchResultsWhenSearchingByTeam() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "N03TST"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].username", equalTo("test.user")))
				.andExpect(jsonPath("$[0].forenames", equalTo("Test")))
				.andExpect(jsonPath("$[0].surname", equalTo("User")))
				.andExpect(jsonPath("$[0].staffCode", equalTo("N01A001")))
				.andExpect(jsonPath("$[0].teams[*].code", hasItems("N01TST", "N02TST", "N03TST")));
	}

	@Test
	public void nonExistentUserReturns404() throws Exception
	{
		mvc.perform(get("/api/user/nonexistent-user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isNotFound());
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
	public void addUser() throws Exception
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
				.andExpect(jsonPath("$.created.username", is("test.user")))
				.andExpect(jsonPath("$.created.at", isWithin(5, SECONDS).of(now())))
				.andExpect(jsonPath("$.updated.username", is("test.user")))
				.andExpect(jsonPath("$.updated.at", isWithin(5, SECONDS).of(now())));
	}

	@Test
	public void usersAreFilteredOnDatasets() throws Exception
	{
		// Attempt to lookup N02 user, as a non-national N01 user
		mvc.perform(get("/api/user/Joe.Bloggs")
				.header("Authorization", "Bearer " + token(mvc, "test.user.local")))
				.andExpect(status().isNotFound());
	}

	@Test
	public void updateUser() throws Exception
	{
		String username = nextTestUsername();
		String token = token(mvc);
		User user = aValidUser().toBuilder()
				.username(username)
				.forenames("Test")
				.surname("User4")
				.staffCode("N01C999")
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.privateSector(false)
				.homeArea(Dataset.builder().code("N01").build())
				.startDate(LocalDate.of(2001, 2, 3))
				.endDate(LocalDate.of(2001, 4, 4))
				.teams(singletonList(Team.builder().code("N01TST").build()))
				.datasets(asList(
						Dataset.builder().code("N01").build(),
						Dataset.builder().code("N02").build()))
				.subContractedProvider(Dataset.builder().code("N01SC2").build())
				.roles(singletonList(Role.builder()
						.name("UMBT001")
						.build()))
				.build();

		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
				.andExpect(status().isCreated())
				.andExpect(redirectedUrl("/user/" + username));

		Thread.sleep(5000); // small wait to test the difference in created/updated date

		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user.toBuilder()
						.email("test2@test.com")
						.forenames("A B C")
						.surname("ABC")
						.staffCode("N01B999")
						.startDate(LocalDate.of(2001, 2, 3))
						.endDate(LocalDate.of(2001, 4, 4))
						.datasets(asList(
								Dataset.builder().code("N01").build(),
								Dataset.builder().code("C02").build(),
								Dataset.builder().code("C03").build()))
						.subContractedProvider(Dataset.builder().code("N01SC3").build())
						.teams(singletonList(Team.builder().code("N02TST").build()))
						.homeArea(Dataset.builder().code("N01").build())
						.privateSector(false)
						.roles(singletonList(Role.builder().name("UMBT002").build()))
						.build())))
				.andExpect(status().isNoContent());

		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("test2@test.com")))
				.andExpect(jsonPath("$.forenames", is("A B C")))
				.andExpect(jsonPath("$.surname", is("ABC")))
				.andExpect(jsonPath("$.staffCode", is("N01B999")))
				.andExpect(jsonPath("$.startDate", is("2001-02-03")))
				.andExpect(jsonPath("$.datasets", hasSize(3)))
				.andExpect(jsonPath("$.datasets[*].code", hasItems("N01", "C02", "C03")))
				.andExpect(jsonPath("$.subContractedProvider.code", is("N01SC3")))
				.andExpect(jsonPath("$.teams", hasSize(1)))
				.andExpect(jsonPath("$.teams[*].code", hasItem("N02TST")))
				.andExpect(jsonPath("$.roles", hasSize(1)))
				.andExpect(jsonPath("$.roles[0].name", is("UMBT002")))
				.andExpect(jsonPath("$.created.at", not(isWithin(5, SECONDS).of(now()))))
				.andExpect(jsonPath("$.updated.at", isWithin(5, SECONDS).of(now())));
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
	public void emailAddressIsReturned() throws Exception
	{
		String token = token(mvc);

		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("test.user@test.com")));
	}

	@Test
	public void userCanBeRenamed() throws Exception
	{
		String username = nextTestUsername();
		String token = token(mvc);

		// Given
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username)
						.build())))
				.andExpect(status().isCreated());

		// When
		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username + "-renamed")
						.build())))
				.andExpect(status().isNoContent());

		// Then
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
		mvc.perform(get("/api/user/" + username + "-renamed")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(username + "-renamed")));
	}

	@Test
	public void userCannotBeRenamedIfNewUsernameAlreadyExists() throws Exception
	{
		String username = nextTestUsername();
		String token = token(mvc);

		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username)
						.build())))
				.andExpect(status().isCreated());

		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username("test.user")
						.build())))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void userWithStaffCodeCanBeRenamed() throws Exception
	{
		String username = nextTestUsername();
		User user = aValidUser().toBuilder()
				.username(username)
				.staffCode("N01A501")
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.build();

		String token = token(mvc);

		// Given
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
				.andExpect(status().isCreated());

		// When
		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user.toBuilder()
						.username(username + "-renamed")
						.build())))
				.andExpect(status().isNoContent());

		// Then
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
		mvc.perform(get("/api/user/" + username + "-renamed")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(username + "-renamed")))
				.andExpect(jsonPath("$.staffCode", is("N01A501")));
	}

	@Test
	public void staffCodeCanBeUpdatedToAnExistingValue() throws Exception
	{
		String username1 = nextTestUsername();
		User user1 = aValidUser().toBuilder()
				.username(username1)
				.staffCode("N01B501")
				.staffGrade(ReferenceData.builder().code("GRADE1").description("Grade 1").build())
				.teams(singletonList(Team.builder().code("N01TST").build()))
				.build();
		String username2 = nextTestUsername();
		User user2 = aValidUser().toBuilder()
				.username(username2)
				.staffCode("N01B502")
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.teams(singletonList(Team.builder().code("N02TST").build()))
				.build();

		String token = token(mvc);

		// Given 2 users with staff codes
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user1)))
				.andExpect(status().isCreated());
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user2)))
				.andExpect(status().isCreated());

		// When I update user 2's staff code to that of user 1
		mvc.perform(post("/api/user/" + username2)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user2.toBuilder()
						.staffCode("N01B501")
						.staffGrade(ReferenceData.builder().code("GRADE1").description("Grade 1").build())
						.teams(singletonList(Team.builder().code("N01TST").build()))
						.build())))
				.andExpect(status().isNoContent());

		// Then user 2's staff code is updated, and user 1 has no staff code
		mvc.perform(get("/api/user/" + username2)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.staffCode", is("N01B501")))
				.andExpect(jsonPath("$.staffGrade.code", is("GRADE1")))
				.andExpect(jsonPath("$.teams[*]", hasSize(1)))
				.andExpect(jsonPath("$.teams[0].code", is("N01TST")));
		mvc.perform(get("/api/user/" + username1)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.staffCode", isEmptyOrNullString()));
	}

	@Test
	public void subContractedProviderIsReturned() throws Exception
	{
		String token = token(mvc);

		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.subContractedProvider.code", is("N01SC1")));
	}

	@Test
	public void userWithEmailAddressCanBeRenamed() throws Exception
	{
		String username = nextTestUsername();
		String token = token(mvc);

		// Given
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username)
						.email(username + "@test.test")
						.build())))
				.andExpect(status().isCreated());

		// When
		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username(username + "-renamed")
						.email(username + "@test.test")
						.build())))
				.andExpect(status().isNoContent());

		// Then
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
		mvc.perform(get("/api/user/" + username + "-renamed")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is(username + "-renamed")))
				.andExpect(jsonPath("$.email", is(username + "@test.test")));
	}

	@Test
	@DirtiesContext
	public void authenticationIsReassertedAfterUsernameIsChanged() throws Exception
	{
		String token = token(mvc);

		mvc.perform(post("/api/user/test.user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser().toBuilder()
						.username("test.user-renamed")
						.build())))
				.andExpect(status().isNoContent());

		mvc.perform(get("/api/whoami")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void searchDoesntReturnInactiveUsersByDefault() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test.user"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", hasItem("test.user")))
				.andExpect(jsonPath("$[*].username", not(hasItem("test.user.inactive"))))
				.andExpect(jsonPath("$[*].username", not(hasItem("test.user.inactive.dbonly"))))
				.andExpect(jsonPath("$[*].username", not(hasItem("test.user.inactive.oidonly"))));
	}

	@Test
	public void inactiveUsersAreReturnedWhenFlagIsSpecified() throws Exception
	{
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test.user")
				.param("includeInactiveUsers", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", hasItems(
						"test.user", "test.user.inactive", "test.user.inactive.dbonly", "test.user.inactive.oidonly")));
	}

	@Test
	public void userRolesHaveDescriptionsRegardlessOfAdminAccess() throws Exception
	{
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
	public void handlingNullCreatedUpdatedDetails() throws Exception
	{
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.created", is(nullValue())))
				.andExpect(jsonPath("$.updated", is(nullValue())));
	}

	@Test
	public void oracleStartAndEndDatesAreReturnedByDefault() throws Exception
	{
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.startDate", is("2000-01-01")));
	}

	@Test
	public void localAdminCannotUpdateNationalAdmin() throws Exception
	{
		// Given I login as a local admin
		String token = token(mvc, "test.user.local");

		// When I attempt to update a national admin
		mvc.perform(post("/api/user/test.user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(aValidUser())))

		// Then I should receive an error message
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("Insufficient permissions to update National users")));
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
	public void groupsAreReturned() throws Exception
	{
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.groups", hasSize(3)));
	}
}