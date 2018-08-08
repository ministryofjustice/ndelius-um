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
public class ProbationAreaUserId implements Serializable
{
	@ManyToOne
	@JoinColumn(name = "PROBATION_AREA_ID")
	private ProbationAreaEntity probationArea;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private UserEntity user;
}
