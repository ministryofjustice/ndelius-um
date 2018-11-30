package uk.co.bconline.ndelius.model.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
@Builder
@ToString(of = {"username", "authorities"})
public class UserPrincipal implements UserDetails {
    private String username;
    private String password;
    private List<UserInteraction> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    @Builder.Default
    private boolean enabled = true;
}
