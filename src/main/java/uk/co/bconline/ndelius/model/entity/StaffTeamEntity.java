package uk.co.bconline.ndelius.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STAFF_TEAM")
public class StaffTeamEntity implements Serializable {
    @EmbeddedId
    private StaffTeamId id;

    @ManyToOne
    @JoinColumn(name = "STAFF_ID", insertable = false, updatable = false)
    private StaffEntity staff;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
    private TeamEntity team;

    @Column(name = "CREATED_BY_USER_ID")
    private Long createdById;

    @ManyToOne
    @JoinColumn(name = "CREATED_BY_USER_ID", insertable = false, updatable = false)
    private UserEntity createdBy;

    @Column(name = "CREATED_DATETIME")
    private LocalDateTime createdAt;

    @Column(name = "LAST_UPDATED_USER_ID")
    private Long updatedById;

    @ManyToOne
    @JoinColumn(name = "LAST_UPDATED_USER_ID", insertable = false, updatable = false)
    private UserEntity updatedBy;

    @Column(name = "LAST_UPDATED_DATETIME")
    private LocalDateTime updatedAt;
}
