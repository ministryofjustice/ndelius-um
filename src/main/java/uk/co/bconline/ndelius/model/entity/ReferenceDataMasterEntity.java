package uk.co.bconline.ndelius.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Entity
@Table(name = "R_REFERENCE_DATA_MASTER")
@SequenceGenerator(name = "REFERENCE_DATA_MASTER_ID_SEQ", sequenceName = "REFERENCE_DATA_MASTER_ID_SEQ", allocationSize = 1)
public class ReferenceDataMasterEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "REFERENCE_DATA_MASTER_ID_SEQ")
    @Column(name = "REFERENCE_DATA_MASTER_ID")
    private long referenceMasterId;

    @Column(name = "CODE_SET_NAME", unique = true, nullable = false)
    private String codeSetName;
}
