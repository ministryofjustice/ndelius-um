package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class StaffTeamId implements Serializable
{
	@ManyToOne
	@JoinColumn(name = "STAFF_ID", nullable = false)
	private StaffEntity staff;

	@ManyToOne
	@JoinColumn(name = "TEAM_ID", nullable = false)
	private TeamEntity team;
}
