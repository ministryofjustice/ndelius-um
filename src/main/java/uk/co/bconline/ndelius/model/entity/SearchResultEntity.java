package uk.co.bconline.ndelius.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Comparator.comparing;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SearchResultEntity
{
	@Id
	private String id;

	@Column(name = "DISTINGUISHED_NAME")
	private String username;

	@Column(name = "FORENAME")
	private String forename;

	@Column(name = "FORENAME2")
	private String forename2;

	@Column(name = "SURNAME")
	private String surname;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@Column(name = "STAFF_CODE")
	private String staffCode;

	@Column(name = "TEAM_CODE")
	private String teamCode;

	@Column(name = "TEAM_DESCRIPTION")
	private String teamDescription;

	@Column(name = "SCORE")
	private float score;

	@Transient
	@Builder.Default
	private Set<TeamEntity> teams = new TreeSet<>(comparing(TeamEntity::getDescription));
}
