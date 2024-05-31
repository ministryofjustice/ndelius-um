package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import uk.co.bconline.ndelius.validator.*;

import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(NON_NULL)
@ValidDates
@AssignableRoles
@AssignableGroups
@AssignableDatasets
@RestrictNationalUsers
@ConditionallyRequired(ifPopulated = "staffCode", required = "staffGrade")
@ConditionallyRequired(ifPopulated = "staffGrade", required = "staffCode")
@ConditionallyRequired(ifPopulated = "teams", required = "staffCode")
@ConditionallyRequired(ifPopulated = "subContractedProvider", required = "staffCode")
@ConditionallyRequired(ifPopulated = "staffCode", required = "startDate")
public class User {
	@NotBlank
	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9_\\-.']*$",
			message = "must be unique and contain only alphanumeric characters, hyphens, apostrophes or full-stops")
	private String username;

	@Setter
	@Transient
	@JsonIgnore
	private String existingUsername;

	@NotBlank
	@Size(max = 71)
	private String forenames;

	@NotBlank
	@Size(max = 35)
	private String surname;

	@Size(max = 255)
	private String email;

	@Size(max = 35)
	@Pattern(regexp = "^[0-9 ]*$",
			message = "must contain only numbers and spaces")
	private String telephoneNumber;

	@NotNull
	private Boolean privateSector;

	@Setter
	@PrefixMatchesProviderCode
	@Pattern(regexp = "^$|^[A-Z0-9]{7}$",
			message = "must consist of 7 alphanumeric characters, however the recommended format is 3 alphanumeric characters followed by one letter and three numbers eg. XXXA001")
	private String staffCode;

	@Valid
	private ReferenceData staffGrade;

	private Dataset subContractedProvider;

	@NotNull
	private Dataset homeArea;

	@DateRange
	private LocalDate startDate;

	@DateRange
	private LocalDate endDate;

	private List<@Valid Team> teams;

	@NotEmpty
	private List<@Valid Dataset> datasets;

	private List<@Valid Dataset> establishments;

	private List<@Valid Role> roles;

	private Map<String, List<@Valid Group>> groups;

	@Schema(accessMode = READ_ONLY)
	private ChangeNote created;

	@Schema(accessMode = READ_ONLY)
	private ChangeNote updated;

	@Size(max = 4000)
	@Schema(description = "Only used for create/update, for adding change notes to a user. " +
			"This will always be blank when fetching a user - to retrieve user change notes, see the /user/{username}/history endpoint.")
	private String changeNote;

	@JsonIgnore
	private List<String> sources;
}