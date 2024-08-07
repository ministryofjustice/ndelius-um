package uk.co.bconline.ndelius.model.entity.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

import static java.time.LocalDate.now;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TEAM")
public class TeamExportEntity implements Serializable {
	@Id
	@Column(name = "TEAM_ID")
	@GeneratedValue(generator = "TEAM_ID_SEQ")
	@SequenceGenerator(name = "TEAM_ID_SEQ", sequenceName = "TEAM_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@ManyToOne
	@JoinColumn(name = "LOCAL_DELIVERY_UNIT_ID")
	private LDUExportEntity localDeliveryUnit;

	@ManyToOne
	@JoinColumn(name = "DISTRICT_ID")
	private BoroughExportEntity borough;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	public String getExportDescription()
	{
		return description + " (" + code + ") " + ((getEndDate() != null && getEndDate().isBefore(now())) ? " [Inactive]" : " [Active]");
	}

	public String getLDUDescription()
	{
		if (localDeliveryUnit != null)
		{
			return localDeliveryUnit.getDescription() + " (" + localDeliveryUnit.getCode() + ") " + ((localDeliveryUnit.getEndDate() != null && localDeliveryUnit.getEndDate().isBefore(now())) ? " [Inactive]" : " [Active]");
		} else {
			return null;
		}
	}

	public String getBoroughDescription()
	{
		if (borough != null)
		{
			return borough.getDescription() + " (" + borough.getCode() + ") " + ((borough.getEndDate() != null && borough.getEndDate().isBefore(now())) ? " [Inactive]" : " [Active]");
		} else {
			return null;
		}
	}
}
