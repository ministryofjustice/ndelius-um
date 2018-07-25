package uk.co.bconline.ndelius.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Role
{
	private String name;
	private String description;
	private List<String> interactions;
}