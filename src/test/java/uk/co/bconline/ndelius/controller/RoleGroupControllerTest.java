package uk.co.bconline.ndelius.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.bconline.ndelius.test.util.AuthUtils.token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class RoleGroupControllerTest {
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
    public void transactionGroupsAreReturned() throws Exception
    {
        mvc.perform(get("/api/rolegroups")
                .header("Authorization", "Bearer " + token(mvc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].name", hasItem("AP Manager")));
    }

    @Test
    public void transactionGroupIsReturned() throws Exception
    {
        mvc.perform(get("/api/rolegroup/AP Manager")
                .header("Authorization", "Bearer " + token(mvc)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$.name", is("AP Manager")))
                .andExpect(jsonPath("$.roles", hasSize(4)))
                .andExpect(jsonPath("$.roles[*].name", hasItems("APBT002","APBT005","APBT050","CABT011")));
    }

}
