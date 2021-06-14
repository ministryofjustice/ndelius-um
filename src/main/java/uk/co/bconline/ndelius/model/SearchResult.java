package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "username")
public final class SearchResult {
	@CsvBindByPosition(position = 0)
	private String username;

	@CsvBindByPosition(position = 1)
	private String forenames;

	@CsvBindByPosition(position = 2)
	private String surname;

	@CsvIgnore // this field isn't always populated, so it is excluded from the export for consistency
	private String email;

	@CsvBindByPosition(position = 3)
	private LocalDate endDate;

	@CsvBindByPosition(position = 4)
	private String staffCode;

	@CsvBindAndSplitByPosition(elementType = Team.class, position = 5)
	private List<Team> teams;

	@CsvIgnore
	private List<String> sources;

	@CsvIgnore
	@JsonIgnore
	private float score;
}