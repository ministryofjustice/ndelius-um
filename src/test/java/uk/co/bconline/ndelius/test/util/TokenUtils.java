package uk.co.bconline.ndelius.test.util;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TokenUtils
{
	public static String token(MockMvc mvc) throws Exception
	{
		return token(mvc, "test.user");
	}

	public static String token(MockMvc mvc, String username) throws Exception
	{
		return mvc.perform(post("/api/login")
				.with(httpBasic(username, "secret")))
				.andReturn()
				.getResponse()
				.getCookie("my-cookie").getValue();
	}
}
