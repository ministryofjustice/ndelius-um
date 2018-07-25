package uk.co.bconline.ndelius.model.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANISATION")
public class OrganisationEntity
{
	public OrganisationEntity(Long id)
	{
		this.id = id;
	}

	@Id
	@Column(name = "ORGANISATION_ID")
	@GeneratedValue(generator = "ORGANISATION_ID_SEQ")
	@SequenceGenerator(name = "ORGANISATION_ID_SEQ", sequenceName = "ORGANISATION_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;
}
