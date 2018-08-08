package uk.co.bconline.ndelius.controller;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.AuthUtils.token;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.bconline.ndelius.model.*;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerTest
{
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private BasicAuthenticationFilter basicAuthenticationFilter;

	@Autowired
	private OncePerRequestFilter jwtAuthenticationFilter;

	private MockMvc mvc;

	@Before
	public void setup()
	{
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.addFilter(jwtAuthenticationFilter)
				.addFilter(basicAuthenticationFilter)
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
				.andExpect(jsonPath("$", hasSize(2)))
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
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "Jim.Bloggs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", not(hasItem("Jim.Bloggs"))));
	}

	@Test
	public void combinedUserIsReturned() throws Exception
	{
		mvc.perform(get("/api/user/test.user")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", equalTo("test.user")))
				.andExpect(jsonPath("$.forenames", equalTo("Test")))			// From OID
				.andExpect(jsonPath("$.surname", equalTo("User")))				// From OID
				.andExpect(jsonPath("$.organisation.code", equalTo("NPS")));	// From DB
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
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(User.builder()
						.forenames("Test")
						.surname("User1")
						.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
						.homeArea(Dataset.builder().code("C01").description("CRC London").build())
						.build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("username: must not be blank")));
	}

	@Test
	public void addUserWithExistingUsername() throws Exception
	{
		String token = token(mvc);
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(User.builder()
						.username("test.user")
						.forenames("Test")
						.surname("User1")
						.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
						.homeArea(Dataset.builder().code("C01").description("CRC London").build())
						.privateSector(false)
						.build())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error[*]", hasItem("username: already exists")));
	}

	@Test
	public void addUser() throws Exception
	{
		String token = token(mvc);
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(User.builder()
						.username("test.user1")
						.forenames("Test")
						.surname("User1")
						.staffCode("N01A999")
						.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
						.privateSector(false)
						.homeArea(Dataset.builder().code("N01").build())
						.startDate(LocalDate.of(2000, 1, 1))
						.endDate(LocalDate.of(2001, 2, 2))
						.organisation(Organisation.builder()
								.code("NPS")
								.build())
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
				.andExpect(redirectedUrl("/user/test.user1"));

		mvc.perform(get("/api/user/test.user1")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username", is("test.user1")))
				.andExpect(jsonPath("$.staffCode", is("N01A999")))
				.andExpect(jsonPath("$.staffGrade.code", is("GRADE2")))
				.andExpect(jsonPath("$.startDate", is("2000-01-01")))
				.andExpect(jsonPath("$.organisation.code", is("NPS")))
				.andExpect(jsonPath("$.datasets[*].code", hasItems("N01", "N02")))
				.andExpect(jsonPath("$.teams[0].code", is("N01TST")))
				.andExpect(jsonPath("$.homeArea.code", is("N01")))
				.andExpect(jsonPath("$.roles", hasSize(1)))
				.andExpect(jsonPath("$.roles[0].name", is("UMBT001")))
				.andExpect(jsonPath("$.roles[0].interactions", hasItem("UMBI001")));
	}

	@Test
	public void addUserWithAliasUsername() throws Exception
	{
		String token = token(mvc);
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(User.builder()
						.username("test.user2")
						.aliasUsername("test.user2.alias")
						.privateSector(false)
						.homeArea(Dataset.builder().code("N01").build())
						.datasets(singletonList(Dataset.builder().code("C01").description("CRC London").build()))
						.forenames("Test")
						.surname("User2")
						.build())))
				.andExpect(status().isCreated());

		mvc.perform(get("/api/user/test.user2")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.aliasUsername", is("test.user2.alias")));
	}

	@Test
	public void usersAreFilteredOnDatasets() throws Exception
	{
		String token = token(mvc);
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(User.builder()
						.username("test.user3")
						.homeArea(Dataset.builder().code("C01").build())
						.privateSector(true)
						.datasets(asList(
								Dataset.builder().code("C02").build(),
								Dataset.builder().code("C03").build()))
						.forenames("Test")
						.surname("User3")
						.build())))
				.andExpect(status().isCreated());

		mvc.perform(get("/api/user/test.user3")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}

	@Test
	public void updateUser() throws Exception
	{
		String token = token(mvc);
		User user = User.builder()
				.username("test.user4")
				.aliasUsername("test.user4.alias")
				.forenames("Test")
				.surname("User4")
				.staffCode("N01A999")
				.staffGrade(ReferenceData.builder().code("GRADE2").description("Grade 2").build())
				.privateSector(false)
				.homeArea(Dataset.builder().code("N01").build())
				.startDate(LocalDate.of(2001, 2, 3))
				.endDate(LocalDate.of(2001, 4, 4))
				.organisation(Organisation.builder().code("NPS").build())
				.teams(singletonList(Team.builder().code("N01TST").build()))
				.datasets(asList(
						Dataset.builder().code("N01").build(),
						Dataset.builder().code("N02").build()))
				.roles(singletonList(Role.builder()
						.name("UMBT001")
						.build()))
				.build();

		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
				.andExpect(status().isCreated())
				.andExpect(redirectedUrl("/user/test.user4"));

		mvc.perform(put("/api/user/test.user4")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user.toBuilder()
						.aliasUsername("ABC")
						.forenames("A B C")
						.surname("ABC")
						.staffCode("N01A999")
						.startDate(LocalDate.of(2001, 2, 3))
						.endDate(LocalDate.of(2001, 4, 4))
						.datasets(asList(
								Dataset.builder().code("N01").build(),
								Dataset.builder().code("C02").build(),
								Dataset.builder().code("C03").build()))
						.teams(singletonList(Team.builder().code("N02TST").build()))
						.homeArea(Dataset.builder().code("N01").build())
						.privateSector(false)
						.organisation(Organisation.builder().code("PO1").build())
						.roles(singletonList(Role.builder().name("UMBT002").build()))
						.build())))
				.andExpect(status().isNoContent());

		mvc.perform(get("/api/user/test.user4")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.aliasUsername", is("ABC")))
				.andExpect(jsonPath("$.forenames", is("A B C")))
				.andExpect(jsonPath("$.surname", is("ABC")))
				.andExpect(jsonPath("$.staffCode", is("N01A999")))
				.andExpect(jsonPath("$.startDate", is("2001-02-03")))
				.andExpect(jsonPath("$.datasets", hasSize(3)))
				.andExpect(jsonPath("$.datasets[*].code", hasItems("N01", "C02", "C03")))
				.andExpect(jsonPath("$.teams", hasSize(1)))
				.andExpect(jsonPath("$.teams[*].code", hasItem("N02TST")))
				.andExpect(jsonPath("$.organisation.code", is("PO1")))
				.andExpect(jsonPath("$.roles", hasSize(1)))
				.andExpect(jsonPath("$.roles[0].name", is("UMBT002")));
	}
}