package uk.co.bconline.ndelius.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This will eventually become the compound user object with data from all sources
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public final class User implements Serializable
{
	private String username;
	private String forenames;
	private String surname;
	private String staffCode;
	private String homeArea;
	private LocalDate endDate;
	private Organisation organisation;
	private List<Team> teams;
	private List<Dataset> datasets;
	private List<Transaction> transactions;
}