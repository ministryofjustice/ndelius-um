package uk.co.bconline.ndelius.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Role implements Serializable
{
	private String name;
	private String description;
	private List<String> interactions;
}