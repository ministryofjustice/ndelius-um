package uk.co.bconline.ndelius.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(NON_NULL)
public class RoleGroup
{
    private String name;
    private List<Role> roles;
}
