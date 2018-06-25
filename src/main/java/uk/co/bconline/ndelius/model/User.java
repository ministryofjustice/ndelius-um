package uk.co.bconline.ndelius.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * This will eventually become the compound user object with data from all sources
 */
@Data
@Builder
public final class User implements Serializable
{
	private String id;
	private String forenames;
	private String surname;
	private List<OIDBusinessTransaction> oidBusinessTransactions;
}