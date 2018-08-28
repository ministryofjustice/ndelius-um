package uk.co.bconline.ndelius.model.entity;

import static javax.persistence.FetchType.EAGER;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;

import lombok.*;
import uk.co.bconline.ndelius.analyzer.CaseInsensitiveWhitespaceAnalyzer;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STAFF")
@Builder(toBuilder = true)
public class StaffEntity
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
	@Field(analyzer = @Analyzer(impl = CaseInsensitiveWhitespaceAnalyzer.class))
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
	@Type(type = "java.time.LocalDate")
	private LocalDate startDate;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@Column(name = "PROBATION_AREA_ID")
	private Long probationAreaId;

	@ManyToOne
	@JoinColumn(name = "STAFF_GRADE_ID")
	private ReferenceDataEntity grade;

	@Setter
	@ContainedIn
	@OneToMany(mappedBy = "staff")
	private Set<UserEntity> user;

	@Column(name = "CREATED_BY_USER_ID")
	private Long createdById;

	@Column(name = "CREATED_DATETIME")
	private LocalDateTime createdAt;

	@Column(name = "LAST_UPDATED_USER_ID")
	private Long updatedById;

	@Column(name = "LAST_UPDATED_DATETIME")
	private LocalDateTime updatedAt;

	@IndexedEmbedded
	@NotFound(action = IGNORE)
	@OneToMany(mappedBy = "staff", fetch = EAGER)
	private Set<StaffTeamEntity> teamLinks = new HashSet<>();

	public Set<TeamEntity> getTeams()
	{
		return teamLinks.stream()
				.map(StaffTeamEntity::getTeam)
				.collect(Collectors.toSet());
	}
}
