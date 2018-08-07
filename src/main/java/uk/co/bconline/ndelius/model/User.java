package uk.co.bconline.ndelius.model;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.*;
import uk.co.bconline.ndelius.validator.AssignableRoles;
import uk.co.bconline.ndelius.validator.ValidDates;
import uk.co.bconline.ndelius.validator.ValidStaffCode;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AssignableRoles
@ValidDates
@ValidStaffCode
public class User
{
	@NotBlank
	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9_\\-.]*$", message = "invalid format")
	private String username;

	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9_\\-.]*$", message = "invalid format")
	private String aliasUsername;

	@NotBlank
	@Size(max = 71)
	private String forenames;

	@NotBlank
	@Size(max = 35)
	private String surname;

	@Pattern(regexp = "^[A-Z0-9]{3}[A-Z][0-9]{3}$", message = "invalid format")
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