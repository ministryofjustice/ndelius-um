package uk.co.bconline.ndelius.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.EAGER;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_")
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = {"probationAreaLinks", "createdBy", "updatedBy", "history"})
public class UserEntity implements Serializable
{
	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(generator = "USER_ID_SEQ")
	@SequenceGenerator(name = "USER_ID_SEQ", sequenceName = "USER_ID_SEQ", allocationSize = 1)
	private Long id;

	@Version
	@Column(name = "ROW_VERSION")
	private Long version;

	@Column(name = "DISTINGUISHED_NAME", unique = true)
	private String username;

	@Column(name = "FORENAME")
	private String forename;

	@Column(name = "FORENAME2")
	private String forename2;

	@Column(name = "SURNAME")
	private String surname;

	@Column(name = "PRIVATE")
	private Boolean privateUser;

	@Column(name = "END_DATE")
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "STAFF_ID")
	private StaffEntity staff;

	@ManyToOne
	@JoinColumn(name = "ORGANISATION_ID")
	private OrganisationEntity organisation;

	@Column(name = "CREATED_BY_USER_ID")
	private Long createdById;

	@ManyToOne
	@JoinColumn(name = "CREATED_BY_USER_ID", insertable = false, updatable = false)
	private UserEntity createdBy;

	@Column(name = "CREATED_DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "LAST_UPDATED_USER_ID")
	private Long updatedById;

	@ManyToOne
	@JoinColumn(name = "LAST_UPDATED_USER_ID", insertable = false, updatable = false)
	private UserEntity updatedBy;

	@Column(name = "LAST_UPDATED_DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "LAST_ACCESSED_DATETIME")
	private LocalDate lastAccessedDate;

	@Setter
	@Builder.Default
	@OneToMany(mappedBy = "user")
	private Set<ChangeNoteEntity> history = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "user", fetch = EAGER)
	private Set<ProbationAreaUserEntity> probationAreaLinks = new HashSet<>();

	public Set<ProbationAreaEntity> getDatasets()
	{
		return ofNullable(probationAreaLinks)
				.map(links -> links.stream()
						.map(ProbationAreaUserEntity::getProbationArea)
						.collect(Collectors.toSet()))
				.orElse(emptySet());
	}

	public Long getNullSafeStaffGradeId()
	{
		return (staff != null && staff.getGrade() != null) ? staff.getGrade().getId() : -1L;
	}

	public String getNullSafeForename2()
	{
		return (forename2 != null && !forename2.isEmpty()) ? forename2 : "";
	}

	public String getNullSafeStaffCode()
	{
		return (staff != null && staff.getCode() != null) ? staff.getCode() : "";
	}
}
