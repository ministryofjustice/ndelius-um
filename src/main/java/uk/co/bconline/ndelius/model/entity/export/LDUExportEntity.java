package uk.co.bconline.ndelius.model.entity.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LOCAL_DELIVERY_UNIT")
public class LDUExportEntity implements Serializable
{
	@Id
	@Column(name = "LOCAL_DELIVERY_UNIT_ID")
	@GeneratedValue(generator = "LOCAL_DELIVERY_UNIT_ID_SEQ")
	@SequenceGenerator(name = "LOCAL_DELIVERY_UNIT_ID_SEQ", sequenceName = "LOCAL_DELIVERY_UNIT_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;
}
