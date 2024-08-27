package uk.co.bconline.ndelius.model.entity.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.co.bconline.ndelius.model.entity.converter.YNConverter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOROUGH")
public class BoroughExportEntity implements Serializable {
	@Id
	@Column(name = "BOROUGH_ID")
	@GeneratedValue(generator = "BOROUGH_ID_SEQ")
	@SequenceGenerator(name = "BOROUGH_ID_SEQ", sequenceName = "BOROUGH_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SELECTABLE")
	@Convert(converter = YNConverter.class)
	private boolean selectable;

	@ManyToOne
	@JoinColumn(name = "PROBATION_AREA_ID")
	private ProbationAreaExportEntity probationArea;

	public String getExportDescription() {
		return description + " (" + code + ")" + (isSelectable() ? "" : " [Inactive]");
	}
}
