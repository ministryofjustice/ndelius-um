package uk.co.bconline.ndelius.config.security;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class WebSecurityGlobalConfigTest
{
	private PasswordEncoder passwordEncoder = new WebSecurityGlobalConfig().passwordEncoder();

	@Test
	public void oidPasswordFormatCanBeMatched()
	{
		byte[] bytes = new LdapShaPasswordEncoder().encode("secret").getBytes();
		String oidPassword = Arrays.toString(bytes).replace("[", "").replace("]", "").replaceAll(" ", "");

		boolean match = passwordEncoder.matches("secret", oidPassword);

		assertTrue(match);
	}

	@Test
	public void normalPasswordFormatCanBeMatched()
	{
		String encodedPassword = new LdapShaPasswordEncoder().encode("secret");

		boolean match = passwordEncoder.matches("secret", encodedPassword);

		assertTrue(match);
	}
}