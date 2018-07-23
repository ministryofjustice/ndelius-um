package uk.co.bconline.ndelius.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Transaction implements Serializable
{
	private String name;
	private String description;
	private List<String> roles;
}