package uk.co.bconline.ndelius.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import lombok.Data;

@Data
@Entity
@Indexed
@Table(name = "USER_")
public class UserEntity implements Serializable
{
	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(generator = "USER_ID_SEQ")
	@SequenceGenerator(name = "USER_ID_SEQ", sequenceName = "USER_ID_SEQ", allocationSize = 1)
	private long id;

	@Field
	@Column(name = "DISTINGUISHED_NAME")
	private String username;

	@Field
	@Column(name = "FORENAME")
	private String forename;

	@Field
	@Column(name = "FORENAME2")
	private String forename2;

	@Field
	@Column(name = "SURNAME")
	private String surname;

	@Column(name = "PRIVATE")
	private boolean privateUser;

	@Column(name = "END_DATE")
	@Type(type = "java.time.LocalDate")
	private LocalDate endDate;

	@OneToOne
	@IndexedEmbedded
	@JoinColumn(name = "STAFF_ID")
	private StaffEntity staff;

	@ManyToOne
	@JoinColumn(name = "ORGANISATION_ID")
	private OrganisationEntity organisation;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private List<DatasetEntity> datasets;
}
