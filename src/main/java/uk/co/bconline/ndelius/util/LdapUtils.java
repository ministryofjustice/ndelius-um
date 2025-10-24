package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasLength;

@Slf4j
@UtilityClass
public class LdapUtils {
    private static final String LDAP_DATE_FORMAT = "yyyyMMdd'000000Z'";

    public static final String OBJECTCLASS = "objectclass";

    public static String fixPassword(String password) {
        if (hasLength(password) && !password.startsWith("{")) {
            // LDAP passes back the password as a stringify'd byte array, so we manually unpick it and turn it back
            // into a hashed string for verification here.
            String[] split = password.split(",");
            byte[] bytes = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                bytes[i] = Byte.valueOf(split[i]);
            }
            return new String(bytes);
        }
        return password;
    }

    public static String randomPassword() {
        val password = RandomStringUtils.secure().next(32);
        if (log.isDebugEnabled()) log.debug("Generating randomized password: {}", password);
        return new LdapShaPasswordEncoder().encode(password);
    }

    public static LocalDate mapLdapStringToDate(String ldapDateString) {
        return ofNullable(ldapDateString)
            .map(s -> LocalDate.parse(s.substring(0, 8), ofPattern(LDAP_DATE_FORMAT.substring(0, 8))))
            .orElse(null);
    }

    public static String mapToLdapString(LocalDate date) {
        return ofNullable(date)
            .map(d -> d.format(ofPattern(LDAP_DATE_FORMAT)))
            .orElse(null);
    }
}
