package uk.co.bconline.ndelius.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class User implements Serializable
{
	private String id;
	private String forenames;
	private String surname;
}