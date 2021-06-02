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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.ContentResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerExportToCSVTest
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
	public void searchQueryIsRequired() throws Exception
	{
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void checkCsvHeader() throws Exception
	{
		String expectedHeader = "\"Username\",\"Forenames\",\"Surname\",\"Team(s)\",\"StaffCode\",\"Sources\",\"EndDate\",\"Email\"";
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test.user"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(startsWith(expectedHeader)));
	}

	@Test
	public void checkCsvHasTestData() throws Exception
	{
		String expectedTestUser = "test.user";
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test.user"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(containsString(expectedTestUser)));
	}

	@Test
	public void checkCsvHasInActiveUserTest() throws Exception
	{
		String expectedTestUser = "test.user.inactive";
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc))
				.param("q", "test.user")
				.param("includeInactiveUsers", "true"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(containsString(expectedTestUser)));
	}

	@Test
	public void csvResultAreFilteredOnDatasets() throws Exception
	{
		String token = token(mvc, "test.user");

		// Given I am filtering on the N01 dataset
		String datasetFilter = "N01";

		// When I search for an N02 user, Then I should get no results
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token)
				.param("q", "Joe.Bloggs")
				.param("dataset", datasetFilter))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(not(containsString("Joe.Bloggs"))));

		// When I search for an N01 user, Then I should get results
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token)
				.param("q", "Tiffiny.Thrasher")
				.param("dataset", datasetFilter))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(containsString("Tiffiny.Thrasher")));
	}

	@Test
	public void CsvDatasetFiltersAreIgnoredIfNotAllowed() throws Exception
	{
		// Given I am non-national user with access only to N01
		String token = token(mvc, "test.user.local");

		// When I attempt to search for N02 users, Then I should get no results
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token)
				.param("q", "")
				.param("dataset", "N02"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(hasLength(0)));

		// When I attempt to search for N01 users, Then I should get results
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token)
				.param("q", "")
				.param("dataset", "N01"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(hasLength(11655)));
	}

	@Test
	public void CsvGetAllUsersInFileshareGroup() throws Exception
	{
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "")
				.param("fileshareGroup", "Group 1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(stringContainsInOrder("Jane.Bloggs", "Joe.Bloggs", "test.user")));
	}

	@Test
	public void CsvGetAllUsersInReportingGroup() throws Exception
	{
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "")
				.param("reportingGroup", "Group 2"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(stringContainsInOrder("Jane.Bloggs", "Joe.Bloggs", "test.user")));
	}

	@Test
	public void CsvFilterOnMultipleGroupsIsInclusive() throws Exception
	{
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "")
				.param("fileshareGroup", "Group 1")		// contains Jane.Bloggs (and test.user)
				.param("reportingGroup", "Group 3"))	// contains Joe.Bloggs
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(stringContainsInOrder("Jane.Bloggs", "Joe.Bloggs", "test.user")));
	}

	@Test
	public void searchQueryWithGroupFilters() throws Exception
	{
		mvc.perform(get("/api/users/export")
				.header("Authorization", "Bearer " + token(mvc, "test.user"))
				.param("q", "j bloggs")
				.param("fileshareGroup", "Group 1")		// contains Jane.Bloggs (and test.user)
				.param("reportingGroup", "Group 3"))	// contains Joe.Bloggs
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv"))
				.andExpect(content().string(stringContainsInOrder("Joe.Bloggs","Jane.Bloggs")));
	}
}