package uk.co.bconline.ndelius.model.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.IndexedEmbedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STAFF_TEAM")
public class StaffTeamEntity
{
	@EmbeddedId
	private StaffTeamId id;

	@ManyToOne
	@ContainedIn
	@JoinColumn(name = "STAFF_ID", insertable = false, updatable = false)
	private StaffEntity staff;

	@ManyToOne
	@IndexedEmbedded
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
