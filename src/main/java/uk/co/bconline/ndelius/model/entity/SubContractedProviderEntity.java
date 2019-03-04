package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cache(usage = READ_ONLY)
@Table(name = "SC_PROVIDER")
public class SubContractedProviderEntity
{
	public SubContractedProviderEntity(Long id)
	{
		this.id = id;
	}

	@Id
	@GeneratedValue(generator = "SC_PROVIDER_ID_SEQ")
	@SequenceGenerator(name = "SC_PROVIDER_ID_SEQ", sequenceName = "SC_PROVIDER_ID_SEQ", allocationSize = 1)
	@Column(name = "SC_PROVIDER_ID")
	private Long id;

	@Column
	private String code;

	@Column
	private String description;

	@Column(name = "ACTIVE_FLAG")
	private Boolean active;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@ManyToOne
	@JoinColumn(name = "PROVIDER_ID")
	private ProbationAreaEntity provider;
}
