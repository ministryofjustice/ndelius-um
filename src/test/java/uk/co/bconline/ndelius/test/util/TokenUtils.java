package uk.co.bconline.ndelius.test.util;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TokenUtils
{
	public static String token(MockMvc mvc) throws Exception
	{
		return token(mvc, "test.user");
	}

	public static String token(MockMvc mvc, String username) throws Exception
	{
		return JsonPath.read(mvc.perform(post("/oauth/token")
				.with(httpBasic(username, "secret"))
				.param("grant_type", "client_credentials"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString(), "access_token");
	}
}
