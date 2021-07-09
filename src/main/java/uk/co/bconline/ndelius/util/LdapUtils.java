package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.bytebuddy.utility.RandomString;
import org.springframework.LdapDataEntry;
import org.springframework.ldap.NoSuchAttributeException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import uk.co.bconline.ndelius.exception.AppException;

import javax.naming.Name;
import java.time.LocalDate;
import java.util.Collections;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Optional.ofNullable;
import static org.springframework.ldap.support.LdapUtils.newLdapName;
import static org.springframework.ldap.support.LdapUtils.removeFirst;
import static org.springframework.util.StringUtils.hasLength;

@Slf4j
@UtilityClass
public class LdapUtils
{
	private static final String LDAP_DATE_FORMAT = "yyyyMMdd'000000Z'";
	private static final String CORRUPTED_USER_ERROR_MESSAGE = "[LDAP: error code 16 - modify/delete: endDate: no such attribute]";

	public static final String OBJECTCLASS = "objectclass";

	public static String fixPassword(String password)
	{
		if (hasLength(password) && !password.startsWith("{"))
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

	public static void handleCorruptedUser(NoSuchAttributeException e, Name userDn,
										   LdapTemplate ldapTemplate, Name ldapBase) {
		if (e.getMessage() != null && e.getMessage().startsWith(CORRUPTED_USER_ERROR_MESSAGE)) {
			log.warn("Attempting to handle NoSuchAttributeException for user " + userDn, e);
			val ctx = ldapTemplate.getContextSource().getReadOnlyContext();
			try {
				// Get current state
				val user = (LdapDataEntry) ldapTemplate.lookup(userDn);
				val entries = Collections.list(ctx.search(userDn, null));

				// Log as much as possible, so the user can be manually recovered in case of failure
				log.info("Rebuilding user: user={}, entries={}", user, entries);

				// Delete user and child entries
				ldapTemplate.unbind(userDn, true);

				// Recreate user
				// TODO test bind instead of create:
				ldapTemplate.bind(userDn, user, user.getAttributes());

				// Recreate child entries (e.g. role associations)
				entries.forEach(entry -> {
					val name = removeFirst(newLdapName(entry.getNameInNamespace()), ldapBase);
					ldapTemplate.bind(name, entry.getObject(), entry.getAttributes());
				});
			} catch (Throwable rebuildException) {
				log.error("Rebuild failure. Error occurred while rebuilding " + userDn, rebuildException);
				log.info("Original exception: ", e);
				throw new AppException("Unable to rebuild user account following data corruption. Please contact the IT support helpdesk.", rebuildException);
			}
		} else {
			log.info("Ignoring NoSuchAttributeException for user " + userDn, e);
			throw e;
		}
	}

}
