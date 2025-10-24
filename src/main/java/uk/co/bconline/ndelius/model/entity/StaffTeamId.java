package uk.co.bconline.ndelius.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StaffTeamId implements Serializable {
    @Setter
    @ManyToOne
    @JoinColumn(name = "STAFF_ID", nullable = false)
    private StaffEntity staff;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID", nullable = false)
    private TeamEntity team;
}
