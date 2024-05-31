package uk.co.bconline.ndelius.model.entity.export;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;

import jakarta.persistence.*;
import java.io.Serializable;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "R_STANDARD_REFERENCE_LIST")
public class ReferenceDataExportEntity implements Serializable {
	@Id
	@Column(name = "STANDARD_REFERENCE_LIST_ID")
	@GeneratedValue(generator = "STANDARD_REFERENCE_LIST_ID_SEQ")
	@SequenceGenerator(name = "STANDARD_REFERENCE_LIST_ID_SEQ", sequenceName = "STANDARD_REFERENCE_LIST_ID_SEQ", allocationSize = 1)
	private Long id;

	@Column(name = "CODE_VALUE", unique = true, nullable = false)
	private String code;

	@Column(name = "CODE_DESCRIPTION")
	private String description;
}
