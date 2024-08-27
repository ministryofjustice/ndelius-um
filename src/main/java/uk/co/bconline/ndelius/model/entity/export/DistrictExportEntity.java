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
@Table(name = "DISTRICT")
public class DistrictExportEntity implements Serializable
{
	@Id
	@Column(name = "DISTRICT_ID")
	@GeneratedValue(generator = "DISTRICT_ID_SEQ")
	@SequenceGenerator(name = "DISTRICT_ID_SEQ", sequenceName = "DISTRICT_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SELECTABLE")
	@Convert(converter = YNConverter.class)
	private boolean selectable;

	@ManyToOne
	@JoinColumn(name = "BOROUGH_ID")
	private BoroughExportEntity borough;

	public String getExportDescription() {
		return description + " (" + code + ") " + (isSelectable() ? " [Active]" : " [Inactive]");
	}
}
