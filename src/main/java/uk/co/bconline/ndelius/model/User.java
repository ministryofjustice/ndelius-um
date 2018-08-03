package uk.co.bconline.ndelius.model;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User
{
	@NotBlank
	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9\\_\\-\\.]*$", message = "username invalid format")
	private String username;

	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9\\_\\-\\.]*$", message = "aliasUsername invalid format")
	private String aliasUsername;

	@NotBlank
	@Size(max = 71)
	private String forenames;

	@NotBlank
	@Size(max = 35)
	private String surname;

	@Size(min = 7, max = 7)
	@Pattern(regexp = "^[A-Z0-9]{3}[A-Z][0-9]{3}$", message = "staff code invalid format")
	private String staffCode;

	private ReferenceData staffGrade;
	private Dataset homeArea;

	private Organisation organisation;

	private LocalDate startDate;
	private LocalDate endDate;

	private List<Team> teams;
	private List<Dataset> datasets;
	private List<Role> roles;
}