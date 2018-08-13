package uk.co.bconline.ndelius.model;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import uk.co.bconline.ndelius.validator.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AssignableRoles
@ValidDates
@HomeAreaNotMatchingStaffCode
@UniqueStaffCode
@MissingStaffCodeWithStaffGrade
@MissingTeams
@MissingStaffGradeWithStaffCode
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

	@NotNull
	private Boolean privateSector;

	@Pattern(regexp = "^[A-Z0-9]{3}[A-Z][0-9]{3}$", message = "invalid format")
	private String staffCode;

	private ReferenceData staffGrade;

	@NotNull
	private Dataset homeArea;

	@NotNull
	private LocalDate startDate;

	private LocalDate endDate;

	@Valid
	private List<Team> teams;

	@Valid
	@NotEmpty
	private List<Dataset> datasets;

	private List<Role> roles;

	@JsonIgnore
	private List<String> sources;
}