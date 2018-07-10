package uk.co.bconline.ndelius.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USER_")
@Data
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_ID_SEQ", allocationSize = 1)
    @Column(name = "USER_ID", unique = true, nullable = false, precision = 38)
    private long userId;

    @Column(name = "END_DATE")
    @Type(type = "java.time.LocalDate")
    private LocalDate endDate;

    private String forename;

    private String forename2;

    private String surname;

    @Column(name = "PRIVATE")
    private boolean privateUser = false;

    @Column(name = "DISTINGUISHED_NAME")
    private String distinguishedName;

    @ManyToOne
    @JoinColumn(name = "ORGANISATION_ID", insertable = false, updatable = false)
    private OrganisationEntity organisation;

    @OneToOne
    @JoinColumn(name = "STAFF_ID", insertable = false, updatable = false)
    private StaffEntity staff;

    @OneToMany(mappedBy="user", fetch=FetchType.EAGER)
    private List<DatasetEntity> datasets;
}
