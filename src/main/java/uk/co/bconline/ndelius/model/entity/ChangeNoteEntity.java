package uk.co.bconline.ndelius.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_NOTE")
public class ChangeNoteEntity implements Serializable {
	@Id
	@Column(name = "USER_NOTE_ID")
	@GeneratedValue(generator = "USER_NOTE_ID_SEQ")
	@SequenceGenerator(name = "USER_NOTE_ID_SEQ", sequenceName = "USER_NOTE_ID_SEQ", allocationSize = 1)
	private Long id;

	@Version
	@Column(name = "ROW_VERSION")
	private Long version;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private UserEntity user;

	@Column(name = "LAST_UPDATED_USER_ID")
	private Long updatedById;

	@ManyToOne
	@JoinColumn(name = "LAST_UPDATED_USER_ID", insertable = false, updatable = false)
	private UserEntity updatedBy;

	@Column(name = "LAST_UPDATED_DATETIME")
	private LocalDateTime updatedAt;

	@Column(name = "NOTES", length = 4000)
	private String notes;
}
