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

import java.util.ArrayList;
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
public class UserControllerSearchTest {
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
	public void searchQueryIsRequired() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void maxPageSizeForSearchIs100() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "")
				.param("pageSize", "101"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void minPageSizeIs1() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "")
				.param("pageSize", "0"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void searchOnName() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "j blog"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].forenames", startsWith("J")))
				.andExpect(jsonPath("$[1].forenames", startsWith("J")));
	}

	@Test
	public void searchOnUsername() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "joe.bloggs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andExpect(jsonPath("$[0].username", is("Joe.Bloggs")));
	}

	@Test
	public void searchOnTeamDescriptionReturnsWholeTeam() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test team"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andExpect(jsonPath("$[*].username", hasItems("test.user", "Joe.Bloggs", "Jane.Bloggs")));
	}

	@Test
	public void searchOnStaffCode() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "N01A001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())))
				.andExpect(jsonPath("$[0].staffCode", is("N01A001")));
	}

	@Test
	public void emptyQueryReturnsNoResult() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", ""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void limitedToPageSize() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "j blog")
				.param("pageSize", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void resultsAreFilteredOnTheCurrentUsersDatasets() throws Exception {
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
	public void combinedUserIsReturnedInSearchResults() throws Exception {
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
	public void combinedUserIsReturnedInSearchResultsWhenSearchingByTeam() throws Exception {
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
	public void inactiveUsersAreNotReturnedByDefault() throws Exception {
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
	public void inactiveUsersAreReturnedWhenFlagIsSpecified() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test.user")
				.param("includeInactiveUsers", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", hasItems(
						"test.user", "test.user.inactive", "test.user.inactive.dbonly", "test.user.inactive.oidonly")));
	}

	@Test
	public void resultsCanBeFilteredOnDatasets() throws Exception {
		String token = token(mvc, "test.user");

		// Given I am filtering on the N01 dataset
		String datasetFilter = "N01";

		// When I search for an N02 user, Then I should get no results
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token)
				.param("q", "Joe.Bloggs")
				.param("dataset", datasetFilter))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", not(hasItem("Joe.Bloggs"))));

		// When I search for an N01 user, Then I should get results
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token)
				.param("q", "Tiffiny.Thrasher")
				.param("dataset", datasetFilter))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", hasItem("Tiffiny.Thrasher")));
	}

	@Test
	public void datasetFiltersAreIgnoredIfNotAllowed() throws Exception {
		// Given I am non-national user with access only to N01
		String token = token(mvc, "test.user.local");

		// When I attempt to search for N02 users, Then I should get no results
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token)
				.param("q", "")
				.param("dataset", "N02"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(empty())));

		// When I attempt to search for N01 users, Then I should get results
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token)
				.param("q", "")
				.param("dataset", "N01"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", not(empty())));
	}

	@Test
	public void resultsAreOrderedAlphabeticallyWhenQueryIsBlank() throws Exception {
		String token = token(mvc, "test.user");

		// When I search on datasets only and don't provide a query string
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token)
				.param("q", "")
				.param("dataset", "N01"))
				.andExpect(status().isOk())
				.andDo(mvcResult -> {
					String json = mvcResult.getResponse().getContentAsString();
					List<String> original = JsonPath.parse(json).read("$[*].username");
					List<String> sorted = new ArrayList<>(original);
					sorted.sort(String::compareToIgnoreCase);
					assertThat(original, is(sorted));
				});
	}

	@Test
	public void getAllUsersInFileshareGroup() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "")
				.param("fileshareGroup", "Group 1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", contains("Jane.Bloggs", "Joe.Bloggs", "test.user")));
	}

	@Test
	public void getAllUsersInReportingGroup() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "")
				.param("reportingGroup", "Group 2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", contains("Jane.Bloggs", "Joe.Bloggs", "test.user")));
	}

	@Test
	public void filterOnMultipleGroupsIsInclusive() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "")
				.param("fileshareGroup", "Group 1")        // contains Jane.Bloggs (and test.user)
				.param("reportingGroup", "Group 3"))    // contains Joe.Bloggs
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", contains("Jane.Bloggs", "Joe.Bloggs", "test.user")));
	}

	@Test
	public void searchQueryWithGroupFilters() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "j bloggs")
				.param("fileshareGroup", "Group 1")        // contains Jane.Bloggs (and test.user)
				.param("reportingGroup", "Group 3"))    // contains Joe.Bloggs
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", containsInAnyOrder("Jane.Bloggs", "Joe.Bloggs")));
	}

	@Test
	public void searchUsersByRole() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "")
				.param("role", "UMBT001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", containsInAnyOrder("test.user", "test.user.local", "test.user.private")));
	}

	@Test
	public void searchQueryWithRoleFilter() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "local")
				.param("role", "UMBT001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", hasItem("test.user.local")));
	}

	@Test
	public void searchQueryIsCaseInsensitive() throws Exception {
		mvc.perform(get("/api/users")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "emma elks")
				.param("includeInactiveUsers", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].username", hasItem("Emma.Elks")));
	}
}
