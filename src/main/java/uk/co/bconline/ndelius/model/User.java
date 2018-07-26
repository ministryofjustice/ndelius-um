package uk.co.bconline.ndelius.model;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public final class User
{
	@NotNull private String username;
	private String aliasUsername;
	private String forenames;
	private String surname;
	private String staffCode;
	private String staffGrade;
	private Dataset homeArea;
	private Organisation organisation;
	private LocalDate startDate;
	private LocalDate endDate;
	private List<Team> teams;
	private List<Dataset> datasets;
	private List<Role> roles;
}