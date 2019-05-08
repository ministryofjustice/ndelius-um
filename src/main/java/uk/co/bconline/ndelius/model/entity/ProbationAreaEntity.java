package uk.co.bconline.ndelius.model.entity;

import lombok.*;
import org.hibernate.annotations.Cache;
import uk.co.bconline.ndelius.model.entity.converter.YNConverter;

import javax.persistence.*;
import java.util.Set;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

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
	@Convert(converter = YNConverter.class)
	private boolean selectable;

	@Column(name = "ESTABLISHMENT")
	@Convert(converter = YNConverter.class)
	private boolean establishment;

	@OneToMany(mappedBy = "probationArea")
	private Set<ProbationAreaUserEntity> userLinks;
}
