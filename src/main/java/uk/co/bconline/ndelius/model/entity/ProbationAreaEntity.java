package uk.co.bconline.ndelius.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import uk.co.bconline.ndelius.model.entity.converter.YNConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

@Entity
@Getter
@NoArgsConstructor
@Cache(usage = READ_ONLY)
@Table(name = "PROBATION_AREA")
@ToString(of = {"code", "description"})
@EqualsAndHashCode(exclude = "userLinks")
public class ProbationAreaEntity implements Serializable
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
