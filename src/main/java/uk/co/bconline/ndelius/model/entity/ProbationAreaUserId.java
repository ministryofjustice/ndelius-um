package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
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
