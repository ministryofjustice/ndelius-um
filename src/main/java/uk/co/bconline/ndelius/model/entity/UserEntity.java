package uk.co.bconline.ndelius.model.entity;

import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

@Getter
@Entity
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

	@JoinColumn(name = "STAFF_ID")
	@ManyToOne(cascade = ALL)
	private StaffEntity staff;

	@ManyToOne
	@JoinColumn(name = "ORGANISATION_ID")
	private OrganisationEntity organisation;

	@Builder.Default
	@NotFound(action = IGNORE)
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
}
