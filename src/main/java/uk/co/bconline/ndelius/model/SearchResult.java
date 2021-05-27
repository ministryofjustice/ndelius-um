package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
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
	@CsvBindByName(column = "Username")
	@CsvBindByPosition(position = 0)
	private String username;

	@CsvBindByName(column = "Forenames")
	@CsvBindByPosition(position = 1)
	private String forenames;

	@CsvBindByName(column = "Surname")
	@CsvBindByPosition(position = 2)
	private String surname;

	@CsvBindByName(column = "Team(s)")
	@CsvBindAndSplitByPosition(elementType = Team.class, position = 3)
	private List<Team> teams;

	@CsvBindByName(column = "StaffCode")
	@CsvBindByPosition(position = 4)
	private String staffCode;

	@CsvBindByName(column = "Sources")
	@CsvBindByPosition(position = 5)
	private List<String> sources;

	@CsvBindByName(column = "EndDate")
	@CsvBindByPosition(position = 6)
	private LocalDate endDate;

	@JsonIgnore
	private float score;
}