package uk.co.bconline.ndelius.model.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
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

	@Field(analyze = Analyze.NO)
	@Column(name = "CODE")
	private String code;

	@Field
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

	@ContainedIn
	@OneToMany(mappedBy = "team")
	private List<StaffTeamEntity> staffLinks;
}
