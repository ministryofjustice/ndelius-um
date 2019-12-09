package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class StaffTeamId implements Serializable
{
	@Setter
	@ManyToOne
	@JoinColumn(name = "STAFF_ID", nullable = false)
	private StaffEntity staff;

	@ManyToOne
	@JoinColumn(name = "TEAM_ID", nullable = false)
	private TeamEntity team;
}
