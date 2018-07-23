package uk.co.bconline.ndelius.model.entity;

import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_")
@Builder(toBuilder = true)
public class UserEntity implements Serializable
{
	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(generator = "USER_ID_SEQ")
	@SequenceGenerator(name = "USER_ID_SEQ", sequenceName = "USER_ID_SEQ", allocationSize = 1)
	private long id;

	@Field
	@Column(name = "DISTINGUISHED_NAME", unique = true)
	private String username;

	@Field
	@Column(name = "FORENAME")
	private String forename;

	@Field
	@Column(name = "FORENAME2")
	private String forename2;

	@Field
	@Column(name = "SURNAME")
	private String surname;

	@Column(name = "PRIVATE")
	private boolean privateUser;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@IndexedEmbedded
	@JoinColumn(name = "STAFF_ID")
	@OneToOne(cascade = CascadeType.ALL)
	private StaffEntity staff;

	@ManyToOne
	@JoinColumn(name = "ORGANISATION_ID")
	private OrganisationEntity organisation;

	@ManyToMany(fetch = EAGER)
	@JoinTable(name = "PROBATION_AREA_USER",
			   joinColumns = @JoinColumn(name = "USER_ID"),
			   inverseJoinColumns = @JoinColumn(name = "PROBATION_AREA_ID"))
	private Set<ProbationAreaEntity> datasets;
}
