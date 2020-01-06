package uk.co.bconline.ndelius.test.util;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

public class TokenUtils
{
	public static String token(MockMvc mvc) throws Exception
	{
		return authCodeToken(mvc, "test.user");
	}

	public static String token(MockMvc mvc, String username) throws Exception
	{
		return authCodeToken(mvc, username);
	}

	public static String getAuthCode(MockMvc mvc, String username) throws Exception
	{
		return fromHttpUrl(requireNonNull(mvc.perform(get("/oauth/authorize")
				.with(httpBasic(username, "secret"))
				.param("client_id", "test.web.client")
				.param("response_type", "code"))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", containsString("?code=")))
				.andReturn()
				.getResponse()
				.getHeader("Location"))).build()
				.getQueryParams()
				.getFirst("code");
	}

	public static String authCodeToken(MockMvc mvc, String username) throws Exception
	{
		String authCode = getAuthCode(mvc, username);

		return JsonPath.read(mvc.perform(post("/oauth/token")
				.with(httpBasic("test.web.client", "secret"))
				.param("code", authCode)
				.param("grant_type", "authorization_code"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString(), "access_token");
	}

	public static String clientCredentialsToken(MockMvc mvc, String clientId) throws Exception
	{
		return JsonPath.read(mvc.perform(post("/oauth/token")
				.with(httpBasic(clientId, "secret"))
				.param("grant_type", "client_credentials"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString(), "access_token");
	}

	public static String implicitToken(MockMvc mvc, String username) throws Exception
	{
		return requireNonNull(mvc.perform(get("/oauth/authorize")
				.with(httpBasic(username, "secret"))
				.param("client_id", "test.web.client")
				.param("response_type", "token"))
				.andExpect(status().isFound())
				.andReturn()
				.getResponse()
				.getHeader("Location"))
				.replaceAll(".*access_token=(.+?)&.*", "$1");
	}
}
