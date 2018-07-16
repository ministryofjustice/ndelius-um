package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

import lombok.Data;

@Data
@Entity
@Table(name = "TEAM")
public class TeamEntity implements Serializable
{
	@Id
	@Column(name = "TEAM_ID")
	@GeneratedValue(generator = "TEAM_ID_SEQ")
	@SequenceGenerator(name = "TEAM_ID_SEQ", sequenceName = "TEAM_ID_SEQ", allocationSize = 1)
	private Long id;

	@Field(analyze = Analyze.NO)
	@Column(name = "CODE")
	private String code;

	@Field
	@Column(name = "DESCRIPTION")
	private String description;

	@ContainedIn
	@ManyToMany(mappedBy = "team")
	private List<StaffEntity> staff;
}
