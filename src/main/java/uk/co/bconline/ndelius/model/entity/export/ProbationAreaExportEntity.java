package uk.co.bconline.ndelius.model.entity.export;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bconline.ndelius.model.entity.converter.YNConverter;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PROBATION_AREA")
public class ProbationAreaExportEntity implements Serializable {
	@Id
	@Column(name = "PROBATION_AREA_ID")
	@GeneratedValue(generator = "PROBATION_AREA_ID_SEQ")
	@SequenceGenerator(name = "PROBATION_AREA_ID_SEQ", sequenceName = "PROBATION_AREA_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SELECTABLE")
	@Convert(converter = YNConverter.class)
	private boolean selectable;

	public String getExportDescription()
	{
		return description + " (" + code + ")" + (selectable ? "" : " [Inactive]");
	}
}
