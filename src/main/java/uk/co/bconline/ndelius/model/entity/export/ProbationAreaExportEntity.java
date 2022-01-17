package uk.co.bconline.ndelius.model.entity.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.io.Serializable;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

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
}
