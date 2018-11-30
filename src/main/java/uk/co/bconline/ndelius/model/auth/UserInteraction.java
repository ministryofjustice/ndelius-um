package uk.co.bconline.ndelius.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@ToString
@AllArgsConstructor
public class UserInteraction implements GrantedAuthority {
    private String authority;
}
