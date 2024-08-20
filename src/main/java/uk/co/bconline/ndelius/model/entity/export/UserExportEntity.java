package uk.co.bconline.ndelius.model.entity.export;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_")
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "username")
public class UserExportEntity implements Serializable
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
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "STAFF_ID")
	private StaffExportEntity staff;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "PROBATION_AREA_USER",
			joinColumns = @JoinColumn(name = "USER_ID"),
			inverseJoinColumns = @JoinColumn(name = "PROBATION_AREA_ID"))
	private List<ProbationAreaExportEntity> datasets;

	@Column(name = "LAST_ACCESSED_DATETIME")
	@Type(type = "java.time.LocalDateTime")
	private LocalDateTime lastAccessedDate;
}
