package uk.co.bconline.ndelius.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "STAFF")
@Data
public class StaffEntity  implements Serializable {

    @Id
    @GeneratedValue(generator = "staff_seq")
    @SequenceGenerator(name = "staff_seq", sequenceName = "STAFF_ID_SEQ", allocationSize = 1)
    @Column(name = "STAFF_ID")
    private Long staffId;

    @ManyToOne()
    @JoinColumn(name = "STAFF_GRADE_ID", insertable = false, updatable = false)
    private ReferenceDataEntity staffGrade;

    @OneToOne(mappedBy = "staff", optional = true, fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(name = "START_DATE")
    @Type(type = "java.time.LocalDate")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    @Type(type = "java.time.LocalDate")
    private LocalDate endDate;

    @Column(name = "OFFICER_CODE")
    private String code;

    @Column(name = "FORENAME")
    private String forename;

    @Column(name = "FORENAME2")
    private String forename2;

    @Column(name = "SURNAME")
    private String surname;
}
