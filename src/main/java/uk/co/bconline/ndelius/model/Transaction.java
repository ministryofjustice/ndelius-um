package uk.co.bconline.ndelius.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public final class Transaction implements Serializable
{
	private String name;
	private List<String> roles;
	private String description;
}