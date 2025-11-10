package uk.co.bconline.ndelius.config.security.auth;

import com.jayway.jsonpath.JsonPath;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.co.bconline.ndelius.test.util.TokenUtils.authCodeToken;
import static uk.co.bconline.ndelius.test.util.TokenUtils.getAuthCode;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class AuthorizationCodeAuthTest {
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
    public void invalidUserCredentialsReturnsUnauthorized() throws Exception {
        mvc.perform(get("/oauth/authorize")
                .with(httpBasic("INVALID", "INVALID"))
                .queryParam("client_id", "test.web.client")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "https://example.com/login-success"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void successfulLoginRedirectsWithAuthorizationCodeInQueryParams() throws Exception {
        mvc.perform(get("/oauth/authorize")
                .with(httpBasic("test.user", "secret"))
                .queryParam("client_id", "test.web.client")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "https://example.com/login-success"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", containsString("?code=")));
    }

    @Test
    public void invalidClientLoginIsUnauthorized() throws Exception {
        String authCode = getAuthCode(mvc, "test.user");

        mvc.perform(post("/oauth/token")
                .with(httpBasic("INVALID", "INVALID"))
                .param("code", authCode)
                .param("grant_type", "authorization_code"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("error").value("invalid_client"));
    }

    @Test
    public void userScopesAreReturnedCorrectly() throws Exception {
        String authCode = getAuthCode(mvc, "test.user", "UMBI001");
        String token = JsonPath.read(mvc.perform(
                post("/oauth/token")
                    .with(httpBasic("test.web.client", "secret"))
                    .param("code", authCode)
                    .param("grant_type", "authorization_code")
                    .param("redirect_uri", "https://example.com/login-success")
            )
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(), "access_token");

        mvc.perform(post("/oauth/check_token")
                .with(httpBasic("test.web.client", "secret"))
                .param("token", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("sub", is("test.user")))
            .andExpect(jsonPath("user_name", is("test.user")))
            .andExpect(jsonPath("client_id", is("test.web.client")))
            .andExpect(jsonPath("scope", containsString("UMBI001")))
            .andExpect(jsonPath("scope", not(containsString("UMBI002"))));
    }

    @Test
    public void authorizationCodeCanBeSwappedForAccessToken() throws Exception {
        String authCode = getAuthCode(mvc, "test.user");

        mvc.perform(post("/oauth/token")
                .with(httpBasic("test.web.client", "secret"))
                .param("code", authCode)
                .param("grant_type", "authorization_code")
                .param("redirect_uri", "https://example.com/login-success"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("token_type", is("Bearer")))
            .andExpect(jsonPath("access_token", notNullValue()));
    }

    @Test
    public void accessingASecureEndpointWithAValidTokenIsAllowed() throws Exception {
        mvc.perform(get("/api/whoami")
                .header("Authorization", "Bearer " + authCodeToken(mvc, "test.user")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("username", is("test.user")));
    }

    @Test
    public void pathBasedRedirectUriCanBeUsed() throws Exception {
        mvc.perform(get("/oauth/authorize")
                .with(httpBasic("test.user", "secret"))
                .queryParam("client_id", "test.web.client")
                .queryParam("redirect_uri", "/login-success")
                .queryParam("response_type", "code"))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", startsWith("/login-success")));
    }
}
