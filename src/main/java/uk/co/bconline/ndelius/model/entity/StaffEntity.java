package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

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

	@ManyToOne
	@JoinColumn(name = "STAFF_GRADE_ID", insertable = false, updatable = false)
	private ReferenceDataEntity staffGrade;

	@ContainedIn
	@OneToOne(mappedBy = "staff")
	private UserEntity user;

	@Column(name = "START_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate startDate;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;
}
