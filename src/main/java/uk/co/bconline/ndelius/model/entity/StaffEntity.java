package uk.co.bconline.ndelius.model.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.type.YesNoConverter;
import jakarta.persistence.Convert;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static jakarta.persistence.FetchType.EAGER;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STAFF")
@Builder(toBuilder = true)
@ToString(exclude = {"teamLinks", "user"})
@EqualsAndHashCode(exclude = {"teamLinks", "user"})
public class StaffEntity implements Serializable
{
	@Id
	@Column(name = "STAFF_ID")
	@GeneratedValue(generator = "STAFF_ID_SEQ")
	@SequenceGenerator(name = "STAFF_ID_SEQ", sequenceName = "STAFF_ID_SEQ", allocationSize = 1)
	private Long id;

	@Version
	@Column(name = "ROW_VERSION")
	private Long version;

	@Column(name = "OFFICER_CODE")
	private String code;

	@Column(name = "FORENAME")
	private String forename;

	@Column(name = "FORENAME2")
	private String forename2;

	@Column(name = "SURNAME")
	private String surname;

	@Column(name = "PRIVATE")
	private Boolean privateStaff;

	@Column(name = "START_DATE")
	private LocalDate startDate;

	@Column(name = "END_DATE")
	private LocalDate endDate;

	@Column(name = "PROBATION_AREA_ID")
	private Long probationAreaId;

	@ManyToOne
	@JoinColumn(name = "STAFF_GRADE_ID")
	private ReferenceDataEntity grade;

	@ManyToOne
	@JoinColumn(name = "SC_PROVIDER_ID")
	private SubContractedProviderEntity subContractedProvider;

	@Setter
	@Builder.Default
	@OneToMany(mappedBy = "staff", fetch = EAGER)
	private Set<UserEntity> user = new HashSet<>();

	@Column(name = "CREATED_BY_USER_ID")
	private Long createdById;

	@Column(name = "CREATED_DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "LAST_UPDATED_USER_ID")
	private Long updatedById;

	@Column(name = "LAST_UPDATED_DATETIME")
	private LocalDateTime updatedAt;

	@Builder.Default
	@NotFound(action = IGNORE)
	@OneToMany(mappedBy = "staff", fetch = EAGER)
	private Set<StaffTeamEntity> teamLinks = new HashSet<>();

	public Set<TeamEntity> getTeams()
	{
		return ofNullable(teamLinks)
				.map(links -> links.stream()
						.map(StaffTeamEntity::getTeam)
						.collect(Collectors.toSet()))
				.orElse(emptySet());
	}
}
