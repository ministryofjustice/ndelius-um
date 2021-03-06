package uk.co.bconline.ndelius.model.entity.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STAFF")
@Builder(toBuilder = true)
public class StaffExportEntity implements Serializable
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
	@Type(type = "java.time.LocalDate")
	private LocalDate startDate;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "STAFF_GRADE_ID")
	private ReferenceDataExportEntity grade;

	@ManyToMany
	@JoinTable(
			name = "STAFF_TEAM",
			joinColumns = @JoinColumn(name = "STAFF_ID"),
			inverseJoinColumns = @JoinColumn(name = "TEAM_ID"))
	private Set<TeamExportEntity> teams;
}
