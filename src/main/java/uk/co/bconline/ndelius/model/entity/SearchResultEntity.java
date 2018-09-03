package uk.co.bconline.ndelius.model.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USER_")
public class SearchResultEntity
{
	@Id
	@Column(name = "DISTINGUISHED_NAME")
	private String username;

	@Column(name = "SCORE")
	private float score;

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		SearchResultEntity that = (SearchResultEntity) o;
		return Objects.equals(username, that.username);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(username);
	}
}
