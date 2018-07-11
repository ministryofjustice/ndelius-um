package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "ORGANISATION")
@Data
public class OrganisationEntity  implements Serializable {

    @Id
    @GeneratedValue(generator = "organisation_seq")
    @SequenceGenerator(name = "organisation_seq", sequenceName = "ORGANISATION_ID_SEQ", allocationSize = 1)
    @Column(name = "ORGANISATION_ID")
    private Long organisationID;

    @Column(name = "CODE")
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;
}
