package uk.co.bconline.ndelius.model.entity;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import lombok.*;

@Getter
@Entity
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_")
@Builder(toBuilder = true)
@EqualsAndHashCode(exclude = "probationAreaLinks")
public class UserEntity
{
	public UserEntity(Long id)
	{
		this.id = id;
	}

	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(generator = "USER_ID_SEQ")
	@SequenceGenerator(name = "USER_ID_SEQ", sequenceName = "USER_ID_SEQ", allocationSize = 1)
	private Long id;

	@Version
	@Column(name = "ROW_VERSION")
	private Long version;

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
	private Boolean privateUser;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@IndexedEmbedded
	@JoinColumn(name = "STAFF_ID")
	@ManyToOne(cascade = ALL)
	private StaffEntity staff;

	@ManyToOne
	@JoinColumn(name = "ORGANISATION_ID")
	private OrganisationEntity organisation;

	@NotFound(action = IGNORE)
	@OneToMany(mappedBy = "user", fetch = EAGER)
	private Set<ProbationAreaUserEntity> probationAreaLinks = new HashSet<>();

	public Set<ProbationAreaEntity> getDatasets()
	{
		return probationAreaLinks.stream()
				.map(ProbationAreaUserEntity::getProbationArea)
				.collect(Collectors.toSet());
	}
}
