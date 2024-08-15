package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bconline.ndelius.model.entity.export.ProbationAreaExportEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOROUGH")
public class BoroughEntity implements Serializable
{
	@Id
	@Column(name = "BOROUGH_ID")
	@GeneratedValue(generator = "BOROUGH_ID_SEQ")
	@SequenceGenerator(name = "BOROUGH_ID_SEQ", sequenceName = "BOROUGH_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@ManyToOne
	@JoinColumn(name = "PROBATION_AREA_ID")
	private ProbationAreaExportEntity probationArea;
}