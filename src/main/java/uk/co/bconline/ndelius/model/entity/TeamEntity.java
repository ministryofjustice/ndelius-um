package uk.co.bconline.ndelius.model.entity;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Cache;

import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
@Cache(usage = READ_ONLY)
@ToString(exclude = "staffLinks")
@EqualsAndHashCode(exclude = "staffLinks")
public class TeamEntity
{
	public TeamEntity(Long id)
	{
		this.id = id;
	}

	@Id
	@Column(name = "TEAM_ID")
	@GeneratedValue(generator = "TEAM_ID_SEQ")
	@SequenceGenerator(name = "TEAM_ID_SEQ", sequenceName = "TEAM_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "START_DATE")
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "END_DATE")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@ManyToOne
	@JoinColumn(name = "PROBATION_AREA_ID", insertable = false, updatable = false)
	private ProbationAreaEntity probationArea;

	@OneToMany(mappedBy = "team")
	private List<StaffTeamEntity> staffLinks;
}
