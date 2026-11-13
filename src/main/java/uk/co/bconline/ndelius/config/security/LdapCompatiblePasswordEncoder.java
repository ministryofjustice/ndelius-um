package uk.co.bconline.ndelius.config.security;

import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.AbstractValidatingPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import uk.co.bconline.ndelius.util.LdapUtils;

final class LdapCompatiblePasswordEncoder extends AbstractValidatingPasswordEncoder {
    @SuppressWarnings("deprecation")
    private final DelegatingPasswordEncoder delegate = new DelegatingPasswordEncoder("bcrypt", Map.of(
        "bcrypt", new BCryptPasswordEncoder(),
        "ldap", new LdapShaPasswordEncoder()
    ));

    @Override
    protected @NonNull String encodeNonNullPassword(@NonNull String rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    protected boolean matchesNonNull(@NonNull String rawPassword, @NonNull String encodedPassword) {
        return delegate.matches(rawPassword, normalize(encodedPassword));
    }

    // Keep automatic upgrades disabled: the LDAP-backed client repository is read-only.
    @Override
    protected boolean upgradeEncodingNonNull(@NonNull String encodedPassword) {
        return false;
    }

    private @NonNull String normalize(@NonNull String encodedPassword) {
        String normalized = LdapUtils.fixPassword(encodedPassword);
        if (normalized.startsWith("{SHA}")
            || normalized.startsWith("{SSHA}")
            || normalized.startsWith("{sha}")
            || normalized.startsWith("{ssha}")) {
            return "{ldap}" + normalized;
        }
        return normalized;
    }
}
