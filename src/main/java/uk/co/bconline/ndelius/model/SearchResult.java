package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "username")
public final class SearchResult
{
	private String username;
	private String forenames;
	private String surname;
	private List<Team> teams;
	private String staffCode;
	private List<String> sources;
	private LocalDate endDate;
	private String email;
	@JsonIgnore
	private float score;
}