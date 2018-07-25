package uk.co.bconline.ndelius.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleGroup
{
    private String name;
    private List<Role> roles;
}
