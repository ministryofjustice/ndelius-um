package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORGANISATION")
public class OrganisationEntity implements Serializable
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
