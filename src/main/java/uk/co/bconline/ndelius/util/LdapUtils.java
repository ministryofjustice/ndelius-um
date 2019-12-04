package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.isEmpty;

@UtilityClass
public class LdapUtils
{
	private static final Logger log = LoggerFactory.getLogger(LdapUtils.class);
	private static final String LDAP_DATE_FORMAT = "yyyyMMdd'000000Z'";

	public static final String OBJECTCLASS = "objectclass";

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

	public static String randomPassword() {
		val password = RandomString.make(32);
		if (log.isDebugEnabled()) log.debug("Generating randomized password: {}", password);
		return new LdapShaPasswordEncoder().encode(password);
	}

	public static LocalDate mapLdapStringToDate(String ldapDateString)
	{
		return ofNullable(ldapDateString)
				.map(s -> LocalDate.parse(s.substring(0, 8), ofPattern(LDAP_DATE_FORMAT.substring(0, 8))))
				.orElse(null);
	}

	public static String mapToLdapString(LocalDate date)
	{
		return ofNullable(date)
				.map(d -> d.format(ofPattern(LDAP_DATE_FORMAT)))
				.orElse(null);
	}
}
