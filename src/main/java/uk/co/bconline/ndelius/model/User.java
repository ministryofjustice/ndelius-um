package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import uk.co.bconline.ndelius.validator.*;

import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

import static io.swagger.annotations.ApiModelProperty.AccessMode.READ_ONLY;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ValidDates
@AssignableRoles
@AssignableDatasets
@RestrictNationalUsers
@ConditionallyRequired(ifPopulated = "staffCode", required = "staffGrade")
@ConditionallyRequired(ifPopulated = "staffGrade", required = "staffCode")
@ConditionallyRequired(ifPopulated = "teams", required = "staffCode")
@ConditionallyRequired(ifPopulated = "subContractedProvider", required = "staffCode")
@ConditionallyRequired(ifPopulated = "staffCode", required = "startDate")
public class User
{
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

	private String email;

	@NotNull
	private Boolean privateSector;

	@PrefixMatchesProviderCode
	@Pattern(regexp = "^$|^[A-Z0-9]{7}$",
			message = "must consist of 7 alphanumeric characters, however the recommended format is 3 alphanumeric characters followed by one letter and three numbers eg. XXXA001")
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

	@Valid
	private List<Dataset> establishments;

	private List<Role> roles;

	private List<Group> groups;

	private Dataset subContractedProvider;

	@ApiModelProperty(accessMode = READ_ONLY)
	private Modification created;

	@ApiModelProperty(accessMode = READ_ONLY)
	private Modification updated;

	@JsonIgnore
	private List<String> sources;
}