package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "PROBATION_AREA")
public class ProbationAreaEntity  implements Serializable 
{
	@Id
	@GeneratedValue(generator = "PROBATION_AREA_ID_SEQ")
	@SequenceGenerator(name = "PROBATION_AREA_ID_SEQ", sequenceName = "PROBATION_AREA_ID_SEQ", allocationSize = 1)
	@Column(name = "PROBATION_AREA_ID")
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@ManyToOne
	@JoinColumn(name = "DIVISION_ID", updatable = false, insertable = false)
	private ProbationAreaEntity division;

	@ManyToOne
	@JoinColumn(name = "ORGANISATION_ID", insertable = false, updatable = false)
	private OrganisationEntity organisation;

	@Column(name = "SELECTABLE")
	private String selectable;
}
