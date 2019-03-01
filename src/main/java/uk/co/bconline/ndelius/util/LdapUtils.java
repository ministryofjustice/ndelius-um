package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.isEmpty;

@UtilityClass
public class LdapUtils
{
	private static final String OID_DATE_FORMAT = "yyyyMMdd'000000Z'";

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

	public static LocalDate mapOIDStringToDate(String oidDateString)
	{
		return ofNullable(oidDateString)
				.map(s -> LocalDate.parse(s.substring(0, 8), ofPattern(OID_DATE_FORMAT.substring(0, 8))))
				.orElse(null);
	}

	public static String mapToOIDString(LocalDate date)
	{
		return ofNullable(date)
				.map(d -> d.format(ofPattern(OID_DATE_FORMAT)))
				.orElse(null);
	}
}
