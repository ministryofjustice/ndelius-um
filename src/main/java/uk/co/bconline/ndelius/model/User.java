package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import uk.co.bconline.ndelius.validator.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ValidDates
@AssignableRoles
@UniqueEmail
@ConditionallyRequired(ifPopulated = "staffCode", required = "staffGrade")
@ConditionallyRequired(ifPopulated = "staffGrade", required = "staffCode")
@ConditionallyRequired(ifPopulated = "teams", required = "staffCode")
@ConditionallyRequired(ifPopulated = "staffCode", required = "startDate")
public class User
{
	@NotBlank
	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9_\\-.']*$",
			message = "must be unique and contain only alphanumeric characters, hyphens, apostrophes or full-stops")
	private String username;

	@NotBlank
	@Size(max = 71)
	private String forenames;

	@NotBlank
	@Size(max = 35)
	private String surname;

	private String email;

	@NotNull
	private Boolean privateSector;

	@Pattern(regexp = "^$|^[A-Z0-9]{3}[A-Z][0-9]{3}$",
			message = "must consist of 3 alphanumeric characters followed by one letter and three numbers eg. XXXA001")
	private String staffCode;

	@Valid
	private ReferenceData staffGrade;

	@NotNull
	private Dataset homeArea;

	@DateRange
	private LocalDate startDate;

	@DateRange
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