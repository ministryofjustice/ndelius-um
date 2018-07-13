package uk.co.bconline.ndelius.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public final class SearchResult implements Serializable
{
	private String username;
	private String forenames;
	private String surname;
	private String staffCode;
}