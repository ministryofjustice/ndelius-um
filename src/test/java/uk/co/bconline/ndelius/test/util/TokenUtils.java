package uk.co.bconline.ndelius.test.util;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MockMvc;

import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;
import static uk.co.bconline.ndelius.util.EncryptionUtils.encrypt;

public class TokenUtils {
    public static String token(MockMvc mvc) throws Exception {
        return token(mvc, "test.user");
    }

    public static String token(MockMvc mvc, String username) throws Exception {
        return authCodeToken(mvc, username);
    }

    public static String getAuthCode(MockMvc mvc, String username) throws Exception {
        return getAuthCode(mvc, username, "UMBT001 UMBI001 UMBI002 UMBI003 UMBI004 UMBI005 UMBI006 UMBI007 UMBI008 UMBI009 UMBI010 UMBI011 UMBI012 UABT0050 UABI020 UABI025");
    }

    public static String getAuthCode(MockMvc mvc, String username, String scopes) throws Exception {
        return fromUriString(requireNonNull(mvc.perform(get("/oauth/authorize")
                .with(httpBasic(username, "secret"))
                .queryParam("client_id", "test.web.client")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "https://example.com/login-success")
                .queryParam("scope", scopes))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", containsString("?code=")))
            .andReturn()
            .getResponse()
            .getHeader("Location"))).build()
            .getQueryParams()
            .getFirst("code");
    }

    public static String authCodeToken(MockMvc mvc, String username) throws Exception {
        String authCode = getAuthCode(mvc, username);

        return JsonPath.read(mvc.perform(post("/oauth/token")
                .with(httpBasic("test.web.client", "secret"))
                .param("code", authCode)
                .param("grant_type", "authorization_code")
                .param("redirect_uri", "https://example.com/login-success"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(), "access_token");
    }

    public static String clientCredentialsToken(MockMvc mvc, String clientId) throws Exception {
        return JsonPath.read(mvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, "secret"))
                .param("grant_type", "client_credentials")
                .param("resource_id", "NDelius"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(), "access_token");
    }

    public static String preAuthenticatedToken(MockMvc mvc, String username) throws Exception {
        return JsonPath.read(mvc.perform(post("/oauth/token")
                .queryParam("u", encrypt(username, "ThisIsASecretKey"))
                .queryParam("t", encrypt(String.valueOf(now().toEpochMilli()), "ThisIsASecretKey"))
                .param("client_id", "test.web.client")
                .param("grant_type", "preauthenticated")
                .param("scope", "UMBI001"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(), "access_token");
    }
}
