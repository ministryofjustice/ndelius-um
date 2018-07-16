package uk.co.bconline.ndelius.model.entity;

import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.IndexedEmbedded;

import lombok.Data;

@Data
@Entity
@Table(name = "STAFF")
public class StaffEntity  implements Serializable {

	@Id
	@Column(name = "STAFF_ID")
	@GeneratedValue(generator = "STAFF_ID_SEQ")
	@SequenceGenerator(name = "STAFF_ID_SEQ", sequenceName = "STAFF_ID_SEQ", allocationSize = 1)
	private Long id;

	@Field(analyze = Analyze.NO)
	@Column(name = "OFFICER_CODE")
	private String code;

	@Column(name = "FORENAME")
	private String forename;

	@Column(name = "FORENAME2")
	private String forename2;

	@Column(name = "SURNAME")
	private String surname;

	@Column(name = "START_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate startDate;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "STAFF_GRADE_ID", insertable = false, updatable = false)
	private ReferenceDataEntity staffGrade;

	@IndexedEmbedded
	@ManyToMany(fetch = EAGER)
	@JoinTable(name = "STAFF_TEAM",
			   joinColumns = @JoinColumn(name = "STAFF_ID"),
			   inverseJoinColumns = @JoinColumn(name = "TEAM_ID"))
	private Set<TeamEntity> team;

	@ContainedIn
	@OneToOne(mappedBy = "staff")
	private UserEntity user;
}
