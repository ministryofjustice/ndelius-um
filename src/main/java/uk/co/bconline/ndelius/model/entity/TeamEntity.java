package uk.co.bconline.ndelius.model.entity;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
@Cache(usage = READ_ONLY)
@ToString(exclude = "staffLinks")
@EqualsAndHashCode(exclude = "staffLinks")
public class TeamEntity implements Serializable
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
	@Type(type = "java.time.LocalDate")
	private LocalDate startDate;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "PROBATION_AREA_ID", insertable = false, updatable = false)
	private ProbationAreaEntity probationArea;

	@OneToMany(mappedBy = "team")
	private List<StaffTeamEntity> staffLinks;
}
