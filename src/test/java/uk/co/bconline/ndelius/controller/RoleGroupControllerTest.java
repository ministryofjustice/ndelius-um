package uk.co.bconline.ndelius.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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
public class RoleGroupControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .alwaysDo(print())
            .build();
    }

    @Test
    public void transactionGroupsAreReturned() throws Exception {
        mvc.perform(get("/api/rolegroups")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[*].name", hasItem("Read Only User")));
    }

    @Test
    public void transactionGroupIsReturned() throws Exception {
        mvc.perform(get("/api/rolegroup/Read Only User")
                .header("Authorization", "Bearer " + token(mvc)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$.name", is("Read Only User")))
            .andExpect(jsonPath("$.roles", not(empty())))
            .andExpect(jsonPath("$.roles[*].name", hasItem("CLBT007")))
            .andExpect(jsonPath("$.roles[*].description", hasItem("Enhanced Search Contact Log")));
    }

}
