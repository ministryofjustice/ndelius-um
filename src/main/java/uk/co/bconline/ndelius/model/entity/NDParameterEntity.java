package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ND_PARAMETER", schema = "delius_app_schema")
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

