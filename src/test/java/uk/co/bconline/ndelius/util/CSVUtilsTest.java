package uk.co.bconline.ndelius.util;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.Test;
import uk.co.bconline.ndelius.model.SearchResult;
import uk.co.bconline.ndelius.model.Team;

import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class CSVUtilsTest {

	@Test
	public void csvHasCorrectHeader() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
		StringWriter writer = new StringWriter();

		CSVUtils.write(List.of(SearchResult.builder().username("test.user").build()), writer);
		String csv = writer.toString();

		String expectedHeader = "\"Username\",\"Forenames\",\"Surname\",\"End Date\",\"Staff Code\",\"Teams\"\n";
		assertThat(csv, startsWith(expectedHeader));
	}

	@Test
	public void csvHasTestData() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
		StringWriter writer = new StringWriter();

		CSVUtils.write(List.of(SearchResult.builder()
				.username("test.user")
				.forenames("test")
				.surname("user")
				.email("email")
				.staffCode("ABC123")
				.teams(List.of(
						Team.builder().code("TEAM01").description("Team 1").build(),
						Team.builder().code("TEAM02").description("Team 2").build()))
				.endDate(LocalDate.of(2020, 1, 1))
				.sources(List.of("LDAP"))
				.build()), writer);
		String csv = writer.toString();

		String expectedTestUser = "\n\"test.user\",\"test\",\"user\",\"2020-01-01\",\"ABC123\",\"Team(code=TEAM01, description=Team 1, providerCode=null) Team(code=TEAM02, description=Team 2, providerCode=null)\"\n";
		assertThat(csv, containsString(expectedTestUser));
	}

	@Test
	public void csvEmptyWithNoSearchResults() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
		StringWriter writer = new StringWriter();

		CSVUtils.write(Collections.emptyList(), writer);
		String csv = writer.toString();

		assertEquals(csv, "");
		assertThat(csv.split("\\n"), arrayWithSize(1));
	}
}