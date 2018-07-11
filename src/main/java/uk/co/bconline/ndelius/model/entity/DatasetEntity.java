package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "PROBATION_AREA_USER")
public class DatasetEntity  implements Serializable {
    @EmbeddedId
    private DatasetKey key;

    @ManyToOne
    @JoinColumn(name = "PROBATION_AREA_ID", insertable = false, updatable = false)
    private ProbationAreaEntity probationArea;

    @ManyToOne
    @JoinColumn(name = "USER_ID", insertable = false, updatable = false)
    private UserEntity user;
}
