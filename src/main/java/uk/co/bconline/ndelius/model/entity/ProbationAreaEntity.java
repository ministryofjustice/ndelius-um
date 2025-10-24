package uk.co.bconline.ndelius.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.co.bconline.ndelius.model.entity.converter.YNConverter;

import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "PROBATION_AREA")
@ToString(of = {"code", "description"})
@EqualsAndHashCode(exclude = "userLinks")
public class ProbationAreaEntity implements Serializable {
    public ProbationAreaEntity(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(generator = "PROBATION_AREA_ID_SEQ")
    @SequenceGenerator(name = "PROBATION_AREA_ID_SEQ", sequenceName = "PROBATION_AREA_ID_SEQ", allocationSize = 1)
    @Column(name = "PROBATION_AREA_ID")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ORGANISATION_ID")
    private Long organisationId;

    @Column(name = "SELECTABLE")
    @Convert(converter = YNConverter.class)
    private boolean selectable;

    @Column(name = "ESTABLISHMENT")
    @Convert(converter = YNConverter.class)
    private boolean establishment;

    @OneToMany(mappedBy = "probationArea")
    private Set<ProbationAreaUserEntity> userLinks;
}
