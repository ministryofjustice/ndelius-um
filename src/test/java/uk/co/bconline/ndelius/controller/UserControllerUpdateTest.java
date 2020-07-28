package uk.co.bconline.ndelius.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.co.bconline.ndelius.model.*;
import uk.co.bconline.ndelius.model.entity.StaffEntity;
import uk.co.bconline.ndelius.repository.db.StaffRepository;

import java.time.LocalDate;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
public class UserControllerUpdateTest
{
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private StaffRepository staffRepository;

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
	public void updatedUserDetailsArePersistedCorrectly() throws Exception
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
				.groups(ImmutableMap.of(
						"Fileshare", singletonList(Group.builder().name("Group 1").type("Fileshare").build())))
				.build();

		// Create user
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
				.andExpect(status().isCreated())
				.andExpect(redirectedUrl("/user/" + username));

		Thread.sleep(5000); // small wait to test the difference in created/updated date

		// Update user
		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user.toBuilder()
						.email("test2@test.com")
						.telephoneNumber("9999")
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
						.groups(ImmutableMap.of(
								"Fileshare", singletonList(Group.builder().name("Group 2").type("Fileshare").build()),
								"NDMIS-Reporting", singletonList(Group.builder().name("Group 1").type("NDMIS-Reporting").build())))
						.build())))
				.andExpect(status().isNoContent());

		// Get user (to check changes)
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("test2@test.com")))
				.andExpect(jsonPath("$.telephoneNumber", is("9999")))
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
				.andExpect(jsonPath("$.groups.Fileshare", hasSize(1)))
				.andExpect(jsonPath("$.groups.NDMIS-Reporting", hasSize(1)))
				.andExpect(jsonPath("$.groups.Fileshare[0].name", is("Group 2")))
				.andExpect(jsonPath("$.groups.NDMIS-Reporting[0].name", is("Group 1")))
				.andExpect(jsonPath("$.created.time", not(isWithin(5, SECONDS).of(now()))))
				.andExpect(jsonPath("$.updated.time", isWithin(5, SECONDS).of(now())));
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
				.andExpect(jsonPath("$.staffCode").doesNotExist());
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
	public void oldStaffRecordIsGivenAnEndDate() throws Exception
	{
		String username = nextTestUsername();
		String token = token(mvc);

		// Given a user with staff code N01A601
		User user = aValidUser().toBuilder()
				.username(username)
				.staffCode("N01A601")
				.staffGrade(ReferenceData.builder().code("GRADE 1").build())
				.build();
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
				.andExpect(status().isCreated());

		// When I update the staff code to N01A602
		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user.toBuilder()
						.staffCode("N01A602")
						.build())))
				.andExpect(status().isNoContent());

		// Then the old staff record (N01A601) should have an end date of today
		Optional<StaffEntity> oldStaff = staffRepository.findByCode("N01A601");
		assertTrue(oldStaff.isPresent());
		assertEquals(oldStaff.get().getEndDate(), LocalDate.now().minus(1, DAYS));
	}

	@Test
	public void staffIsRemovedIfHomeAreaChanges() throws Exception {
		String username = nextTestUsername();
		String token = token(mvc);

		// Given a user with staff code N01A603 and home area N01
		User user = aValidUser().toBuilder()
				.username(username)
				.staffCode("N01A603")
				.staffGrade(ReferenceData.builder().code("GRADE 1").build())
				.build();
		mvc.perform(post("/api/user")
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user)))
				.andExpect(status().isCreated());

		// When I update the home area to N02
		mvc.perform(post("/api/user/" + username)
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().findAndRegisterModules().writeValueAsString(user.toBuilder()
						.homeArea(Dataset.builder().code("N02").build())
						.build())))
				.andExpect(status().isNoContent());

		// Then the staff code should be removed from the user
		mvc.perform(get("/api/user/" + username)
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.homeArea.code", is("N02")))
				.andExpect(jsonPath("$.staffCode").doesNotExist());

		// And the staff record should be end-dated
		Optional<StaffEntity> staff = staffRepository.findByCode("N01A603");
		assertTrue(staff.isPresent());
		assertEquals(staff.get().getEndDate(), LocalDate.now().minus(1, DAYS));
	}
}
