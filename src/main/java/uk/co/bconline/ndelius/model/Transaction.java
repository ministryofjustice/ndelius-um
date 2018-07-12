package uk.co.bconline.ndelius.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class Transaction implements Serializable
{
	private String name;
	private List<String> roles;
}