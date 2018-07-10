package uk.co.bconline.ndelius.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "R_STANDARD_REFERENCE_LIST")
@SequenceGenerator(name="STANDARD_REFERENCE_LIST_ID_SEQ", sequenceName="STANDARD_REFERENCE_LIST_ID_SEQ", allocationSize=1)
@Data
public class ReferenceDataEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="STANDARD_REFERENCE_LIST_ID_SEQ")
    @Column(name = "STANDARD_REFERENCE_LIST_ID")
    private Long referenceDataId;

    @Column(name = "CODE_VALUE", unique = true, nullable = false)
    private String codeValue;

    @Column(name = "CODE_DESCRIPTION")
    private String codeDescription;

    @Column(name = "SELECTABLE", nullable = false)
    private String selectable;
}
