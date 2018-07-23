package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "R_STANDARD_REFERENCE_LIST")
public class ReferenceDataEntity implements Serializable
{
	public ReferenceDataEntity(Long id)
	{
		this.id = id;
	}

	@Id
	@Column(name = "STANDARD_REFERENCE_LIST_ID")
	@GeneratedValue(strategy = GenerationType.AUTO, generator="STANDARD_REFERENCE_LIST_ID_SEQ")
	@SequenceGenerator(name="STANDARD_REFERENCE_LIST_ID_SEQ", sequenceName="STANDARD_REFERENCE_LIST_ID_SEQ", allocationSize=1)
	private Long id;

	@Column(name = "CODE_VALUE", unique = true, nullable = false)
	private String code;

	@Column(name = "CODE_DESCRIPTION")
	private String description;

	@Column(name = "SELECTABLE", nullable = false)
	private String selectable;

    @ManyToOne
    @JoinColumn(name = "REFERENCE_DATA_MASTER_ID", insertable=false, updatable=false)
    private ReferenceDataMasterEntity referenceDataMaster;

    @Column(name = "REFERENCE_DATA_MASTER_ID")
    private Long referenceMasterID;
}
