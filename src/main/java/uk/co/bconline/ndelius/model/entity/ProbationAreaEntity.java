package uk.co.bconline.ndelius.model.entity;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cache;

import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = READ_ONLY)
@Table(name = "PROBATION_AREA")
@ToString(exclude = "userLinks")
@EqualsAndHashCode(exclude = "userLinks")
public class ProbationAreaEntity
{
	public ProbationAreaEntity(Long id)
	{
		this.id = id;
	}

	@Id
	@GeneratedValue(generator = "PROBATION_AREA_ID_SEQ")
	@SequenceGenerator(name = "PROBATION_AREA_ID_SEQ", sequenceName = "PROBATION_AREA_ID_SEQ", allocationSize = 1)
	@Column(name = "PROBATION_AREA_ID")
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "ORGANISATION_ID")
	private Long organisationId;

	@Column(name = "SELECTABLE")
	private String selectable;

	@OneToMany(mappedBy = "probationArea")
	private Set<ProbationAreaUserEntity> userLinks;
}
