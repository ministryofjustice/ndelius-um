package uk.co.bconline.ndelius.model;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "username")
public final class ExportResult {
	@CsvBindByPosition(position = 0)
	private String username;
	@CsvBindByPosition(position = 1)
	private String forenames;
	@CsvBindByPosition(position = 2)
	private String surname;
	@CsvBindByPosition(position = 3)
	private String email;
	@CsvBindByPosition(position = 4)
	private String telephoneNumber;
	@CsvBindByPosition(position = 5)
	private LocalDate startDate;
	@CsvBindByPosition(position = 6)
	private LocalDate endDate;
	@CsvBindByPosition(position = 7)
	private String homeArea;
	@CsvBindByPosition(position = 8)
	private String datasets;
	@CsvBindByPosition(position = 9)
	private String sector;
	@CsvBindByPosition(position = 10)
	private String staffCode;
	@CsvBindByPosition(position = 11)
	private String staffGrade;
	@CsvBindByPosition(position = 12)
	private String teams;
}
