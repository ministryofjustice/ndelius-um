package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class DatasetKey implements Serializable
{
    private static final long serialVersionUID = -3533134137431580642L;

    @Column(name = "PROBATION_AREA_ID")
    private Long probationAreaID;

    @Column(name = "USER_ID")
    private Long userID;
}
