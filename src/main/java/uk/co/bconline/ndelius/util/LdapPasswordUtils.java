package uk.co.bconline.ndelius.util;

import static org.springframework.util.StringUtils.isEmpty;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LdapPasswordUtils
{
	public static String fixPassword(String password)
	{
		if (!isEmpty(password) && !password.startsWith("{"))
		{
			// LDAP passes back the password as a stringify'd byte array, so we manually unpick it and turn it back
			// into a hashed string for verification here.
			String[] split = password.split(",");
			byte[] bytes = new byte[split.length];
			for (int i = 0; i < split.length; i++)
			{
				bytes[i] = Byte.valueOf(split[i]);
			}
			return new String(bytes);
		}
		return password;
	}
}
