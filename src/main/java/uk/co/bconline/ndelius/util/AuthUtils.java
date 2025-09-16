package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import uk.co.bconline.ndelius.model.auth.UserInteraction;
import uk.co.bconline.ndelius.model.entry.RoleEntry;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static uk.co.bconline.ndelius.util.Constants.NATIONAL_ACCESS;

@Slf4j
@UtilityClass
public class AuthUtils {
    public static Authentication me() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String myUsername() {
        return ofNullable(me())
            .map(me -> {
                val principal = me().getPrincipal();
                if (principal instanceof UserDetails) {
                    return ((UserDetails) principal).getUsername();
                } else if (principal instanceof RegisteredClient) {
                    return ((RegisteredClient) principal).getClientId();
                } else if (principal instanceof OAuth2AuthenticatedPrincipal && AuthorizationGrantType.AUTHORIZATION_CODE.equals(((OAuth2AuthenticatedPrincipal) me().getPrincipal()).getAttribute("grant_type"))) {
                    return ((OAuth2AuthenticatedPrincipal) principal).getAttribute("username");
                } else if (principal instanceof OAuth2AuthenticatedPrincipal && AuthorizationGrantType.CLIENT_CREDENTIALS.equals(((OAuth2AuthenticatedPrincipal) me().getPrincipal()).getAttribute("grant_type"))) {
                    return ((OAuth2AuthenticatedPrincipal) principal).getAttribute("client_id");
                } else {
                    return (String) principal;
                }
            })
            .orElse("UNKNOWN");
    }

    public static boolean isClient() {
        val principal = me().getPrincipal();
        return principal instanceof RegisteredClient
            || (principal instanceof OAuth2AuthenticatedPrincipal && AuthorizationGrantType.CLIENT_CREDENTIALS.equals(((OAuth2AuthenticatedPrincipal) principal).getAttribute("grant_type")));
    }

    public static boolean isUser() {
        val principal = me().getPrincipal();
        return principal instanceof UserDetails
            || (principal instanceof OAuth2AuthenticatedPrincipal && AuthorizationGrantType.AUTHORIZATION_CODE.equals(((OAuth2AuthenticatedPrincipal) principal).getAttribute("grant_type")));
    }

    public static String myToken() {
        val details = me().getDetails();
        return null; // TODO check how to get token (used for revoking on logout)
//		if (details instanceof OAuth2AuthenticationDetails) {
//			return ((OAuth2AuthenticationDetails) details).getTokenValue();
//		} else {
//			return null;
//		}
    }

    public static Stream<String> myInteractions() {
        return me().getAuthorities().stream().map(GrantedAuthority::getAuthority);
    }

    public static Stream<String> mapToScopes(Collection<RoleEntry> roles) {
        return ofNullable(roles).stream().flatMap(r -> r.stream()
            .map(role -> asList(
                role.getName(),
                role.getInteractions().toArray(new String[0])))
            .flatMap(List::stream));
    }

    public static Stream<UserInteraction> mapToAuthorities(Collection<RoleEntry> roles) {
        return ofNullable(roles)
            .map(r -> mapToScopes(roles)
                .map(UserInteraction::new))
            .orElseGet(Stream::empty);
    }

    public static boolean isNational() {
        log.debug("Checking national access (UABI025), interactions={}", myInteractions().collect(toSet()));
        return myInteractions().anyMatch(NATIONAL_ACCESS::equals);
    }

    public static String getRequiredScope(PreAuthorize annotation) {
        val matcher = Pattern.compile("#oauth2\\.hasScope\\('(.*)'\\)").matcher(annotation.value());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
