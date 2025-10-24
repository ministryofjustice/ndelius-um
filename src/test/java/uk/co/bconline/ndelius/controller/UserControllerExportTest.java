package uk.co.bconline.ndelius.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.TokenUtils.token;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerExportTest {

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
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void checkCsvHeader() throws Exception {
        String expectedHeader = "\"Username\",\"Forenames\",\"Surname\",\"End Date\",\"Staff Code\",\"Teams\"";
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc))
                .queryParam("q", "test.user"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(startsWith(expectedHeader + "\n")));
    }

    @Test
    public void checkCsvHasTestData() throws Exception {
        String expectedTestUser = "\"test.user\"";
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc))
                .queryParam("q", "test.user"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(containsString(expectedTestUser)));
    }

    @Test
    public void checkCsvHasInActiveUserTest() throws Exception {
        String expectedTestUser = "test.user.inactive";
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc))
                .queryParam("q", "test.user")
                .queryParam("includeInactiveUsers", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(containsString(expectedTestUser)));
    }

    @Test
    public void csvResultAreFilteredOnDatasets() throws Exception {
        String token = token(mvc, "test.user");

        // Given I am filtering on the N01 dataset
        String datasetFilter = "N01";

        // When I search for an N02 user, Then I should get no results
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token)
                .queryParam("q", "Joe.Bloggs")
                .queryParam("dataset", datasetFilter))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(not(containsString("Joe.Bloggs"))));

        // When I search for an N01 user, Then I should get results
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token)
                .queryParam("q", "Tiffiny.Thrasher")
                .queryParam("dataset", datasetFilter))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(containsString("Tiffiny.Thrasher")));
    }

    @Test
    public void CsvDatasetFiltersAreIgnoredIfNotAllowed() throws Exception {
        // Given I am non-national user with access only to N01
        String token = token(mvc, "test.user.local");

        // When I attempt to search for N02 users, Then I should get no results
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token)
                .queryParam("q", "")
                .queryParam("dataset", "N02"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(hasLength(0)));

        // When I attempt to search for N01 users, Then I should get results
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token)
                .queryParam("q", "")
                .queryParam("dataset", "N01"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(not(emptyOrNullString())));
    }

    @Test
    public void CsvGetAllUsersInFileshareGroup() throws Exception {
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc, "test.user"))
                .queryParam("q", "")
                .queryParam("fileshareGroup", "Group 1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(stringContainsInOrder("Jane.Bloggs", "Joe.Bloggs", "test.user")));
    }

    @Test
    public void CsvGetAllUsersInReportingGroup() throws Exception {
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc, "test.user"))
                .queryParam("q", "")
                .queryParam("reportingGroup", "Group 2"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(stringContainsInOrder("Jane.Bloggs", "Joe.Bloggs", "test.user")));
    }

    @Test
    public void CsvFilterOnMultipleGroupsIsInclusive() throws Exception {
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc, "test.user"))
                .queryParam("q", "")
                .queryParam("fileshareGroup", "Group 1")        // contains Jane.Bloggs (and test.user)
                .queryParam("reportingGroup", "Group 3"))    // contains Joe.Bloggs
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(stringContainsInOrder("Jane.Bloggs", "Joe.Bloggs", "test.user")));
    }

    @Test
    public void searchQueryWithGroupFilters() throws Exception {
        mvc.perform(get("/api/users/export")
                .header("Authorization", "Bearer " + token(mvc, "test.user"))
                .queryParam("q", "j bloggs")
                .queryParam("fileshareGroup", "Group 1")        // contains Jane.Bloggs (and test.user)
                .queryParam("reportingGroup", "Group 3"))    // contains Joe.Bloggs
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(stringContainsInOrder("Joe.Bloggs", "Jane.Bloggs")));
    }

    @Test
    public void fullExportIsRestrictedToNationalAdmins() throws Exception {
        mvc.perform(get("/api/users/export/all")
                .header("Authorization", "Bearer " + token(mvc, "Joe.Bloggs")))
            .andExpect(status().isForbidden());
    }

    @Test
    public void dataIsDisplayedCorrectlyForFullExport() throws Exception {
        String expectedHeader = "\"Username\",\"Forenames\",\"Surname\",\"Email\",\"Telephone Number\",\"Start Date\",\"End Date\",\"Last Accessed Delius\",\"Home Area\",\"Datasets\",\"Sector\",\"Staff Code\",\"Staff Grade\",\"Team\",\"LAU\",\"PDU\",\"Provider\",\"Role Descriptions\"";
        String expectedStartDate = now().minusDays(10).format(ISO_LOCAL_DATE);
        String expectedLoginDate = now().format(ISO_LOCAL_DATE);// see data.sql
        String[] expectedUsers = {
            "\n\"Abdul.Austria\",\"Abdul\",\"Austria\",\"\",\"\",\"" + expectedStartDate + "\",\"\",\"\",\"N01\",\"\",\"Public\",\"N01A168\",\"GRADE1\",\"\",\"\",\"\",\"\",\"\"",
            "\n\"Leia.Leaman\",\"Leia\",\"Leaman\",\"\",\"\",\"" + expectedStartDate + "\",\"\",\"\",\"N01\",\"\",\"Public\",\"N01A086\",\"GRADE1\",\"\",\"\",\"\",\"\",\"\"",
            "\n\"Zina.Zenon\",\"Zina\",\"Zenon\",\"\",\"\",\"" + expectedStartDate + "\",\"\",\"\",\"N01\",\"\",\"Public\",\"N01A131\",\"GRADE1\",\"\",\"\",\"\",\"\",\"\"",
            "\n\"test.user\",\"Test\",\"User\",\"test.user@test.com\",\"0123 456 789\",\"2000-01-02\",\"\",\"" + expectedLoginDate + " 00:00:00\",\"N01\",\"N01,N02,N03\",\"Public\",\"N01A001\",\"GRADE1\",\"Another (N03TST),Other team (N02TST),Test team (N01TST)\",\"Local Admin Unit A (LAU1),Local Admin Unit B (LAU2)\",\"Borough A (B1) [Inactive],Borough B (B2)\",\"NPS London (N01),NPS North East (N02)\",\"AP Vacancy Tracker API Admin,Local AP Admin,National AP Admin,National Public Reference Data Admin,National Reference Code List Maintenance,User Administrator\"",
            "\n\"test.user.private\",\"Test\",\"User (Private)\",\"test.user.private@test.com\",\"\",\"\",\"\",\"" + expectedLoginDate + " 00:00:00\",\"C01\",\"C01,C02\",\"Public\",\"\",\"\",\"\",\"\",\"\",\"\",\"Private RBAC Admin,User Administrator\""};

        MvcResult asyncResult = mvc.perform(get("/api/users/export/all")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(request().asyncStarted())
            .andReturn();

        mvc.perform(asyncDispatch(asyncResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/csv"))
            .andExpect(content().string(startsWith(expectedHeader + "\n")))
            .andExpect(content().string(stringContainsInOrder(expectedUsers)))
            .andExpect(content().string(not(containsString("test.user.inactive")))); // inactive users should not be returned
    }
}
