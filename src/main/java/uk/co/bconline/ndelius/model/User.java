package uk.co.bconline.ndelius.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

/**
 * This will eventually become the compound user object with data from all sources
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class User implements Serializable
{
	private String id;
	private String forenames;
	private String surname;
	private List<OIDBusinessTransaction> oidBusinessTransactions;
	private List<Dataset> datasets;
	private Organisation organisation;
}