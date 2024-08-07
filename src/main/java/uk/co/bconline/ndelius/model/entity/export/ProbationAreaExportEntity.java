package uk.co.bconline.ndelius.model.entity.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

import static java.time.LocalDate.now;
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

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	public String getExportDescription()
	{
		return description + " (" + code + ") " + ((getEndDate() != null && getEndDate().isBefore(now())) ? " [Inactive]" : " [Active]");
	}
}
