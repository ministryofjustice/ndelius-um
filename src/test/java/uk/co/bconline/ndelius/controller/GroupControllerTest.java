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
public class GroupControllerTest {
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
    public void allGroupsAreReturned() throws Exception {
        mvc.perform(get("/api/groups")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$['NDMIS-Reporting'][*].description", hasItem("Reporting Group 1")))
            .andExpect(jsonPath("$['NDMIS-Reporting'][*].description", hasItem("Reporting Group 2")))
            .andExpect(jsonPath("$['Fileshare'][*].description", hasItem("Fileshare Group 1")));
    }

    @Test
    public void groupsCanBeFilteredByType() throws Exception {
        mvc.perform(get("/api/groups/fileshare")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[*].description", hasItem("Fileshare Group 1")))
            .andExpect(jsonPath("$[*].description", not(hasItem("Reporting Group 1"))));
    }

    @Test
    public void topLevelGroupIsReturned() throws Exception {
        mvc.perform(get("/api/group/Top-Level Group")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description", is("Top-Level Group")));
    }

    @Test
    public void fileshareGroupIsReturned() throws Exception {
        mvc.perform(get("/api/group/fileshare/Group 1")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description", is("Fileshare Group 1")))
            .andExpect(jsonPath("$.type", equalToIgnoringCase("Fileshare")))
            .andExpect(jsonPath("$.members", hasItem("test.user")))
            .andExpect(jsonPath("$.members", hasItem("Joe.Bloggs")));
    }

    @Test
    public void reportingGroupIsReturned() throws Exception {
        mvc.perform(get("/api/group/ndmis-reporting/Group 1")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description", is("Reporting Group 1")))
            .andExpect(jsonPath("$.type", equalToIgnoringCase("NDMIS-Reporting")))
            .andExpect(jsonPath("$.members", hasItem("test.user")));
    }
}
