package uk.co.bconline.ndelius.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public final class SearchResult implements Serializable
{
	private String username;
	private String forenames;
	private String surname;
	private String staffCode;
}