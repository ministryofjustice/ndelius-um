package uk.co.bconline.ndelius.model.entity;

import static org.hibernate.annotations.CacheConcurrencyStrategy.READ_ONLY;

import javax.persistence.*;

import org.hibernate.annotations.Cache;

import lombok.Getter;

@Getter
@Entity
@Cache(usage = READ_ONLY)
@Table(name = "R_REFERENCE_DATA_MASTER")
@SequenceGenerator(name = "REFERENCE_DATA_MASTER_ID_SEQ", sequenceName = "REFERENCE_DATA_MASTER_ID_SEQ", allocationSize = 1)
public class ReferenceDataMasterEntity 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "REFERENCE_DATA_MASTER_ID_SEQ")
	@Column(name = "REFERENCE_DATA_MASTER_ID")
	private long referenceMasterId;

	@Column(name = "CODE_SET_NAME", unique = true, nullable = false)
	private String codeSetName;
}
