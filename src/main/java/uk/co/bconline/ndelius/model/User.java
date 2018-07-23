package uk.co.bconline.ndelius.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public final class User implements Serializable
{
	@NotNull private String username;
	private String forenames;
	private String surname;
	private String staffCode;
	private String staffGrade;
	private String homeArea;
	private LocalDate startDate;
	private LocalDate endDate;
	private Organisation organisation;
	private List<Team> teams;
	private List<Dataset> datasets;
	private List<Transaction> transactions;
}