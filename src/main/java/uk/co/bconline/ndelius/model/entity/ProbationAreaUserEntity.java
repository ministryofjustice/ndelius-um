package uk.co.bconline.ndelius.model.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PROBATION_AREA_USER")
public class ProbationAreaUserEntity
{
	@EmbeddedId
	private ProbationAreaUserId id;

	@ManyToOne
	@JoinColumn(name = "PROBATION_AREA_ID", insertable = false, updatable = false)
	private ProbationAreaEntity probationArea;

	@ManyToOne
	@JoinColumn(name = "USER_ID", insertable = false, updatable = false)
	private UserEntity user;

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
