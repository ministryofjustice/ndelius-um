package uk.co.bconline.ndelius.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
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
	private LocalDate startDate;

	@Column(name = "END_DATE")
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "PROBATION_AREA_ID", insertable = false, updatable = false)
	private ProbationAreaEntity probationArea;

	@OneToMany(mappedBy = "team")
	private List<StaffTeamEntity> staffLinks;
}
