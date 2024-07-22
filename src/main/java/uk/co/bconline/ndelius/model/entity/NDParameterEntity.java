package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ND_PARAMETER")
public class NDParameterEntity
{
    @Id
    @Column(name = "ND_PARAMETER_ID")
    private Long id;

    @Column(name = "ND_VALUE")
    private Double ndValue;

    @Column(name = "ND_VALUE_STRING")
    private String ndValueString;

    @Column(name = "ND_PARAMETER")
    private String ndParameter;
}

