package uk.co.bconline.ndelius.test.util;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.MockMvc;

public class AuthUtils
{
	public static String token(MockMvc mvc) throws Exception
	{
		return mvc.perform(get("/api/login")
				.with(httpBasic("test.user", "secret")))
				.andReturn()
				.getResponse()
				.getCookie("my-cookie").getValue();
	}
}
