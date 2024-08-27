package uk.co.bconline.ndelius.model;

import com.opencsv.bean.CsvBindByName;
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
	@CsvBindByName(column = "Last Accessed Delius")
	@CsvBindByPosition(position = 7)
	private String lastAccessedDate;
	@CsvBindByPosition(position = 8)
	private String homeArea;
	@CsvBindByPosition(position = 9)
	private String datasets;
	@CsvBindByPosition(position = 10)
	private String sector;
	@CsvBindByPosition(position = 11)
	private String staffCode;
	@CsvBindByPosition(position = 12)
	private String staffGrade;
	@CsvBindByName(column = "Team")
	@CsvBindByPosition(position = 13)
	private String teams;
	@CsvBindByName(column = "LAU")
	@CsvBindByPosition(position = 14)
	private String localAdminUnit;
	@CsvBindByName(column = "PDU")
	@CsvBindByPosition(position = 15)
	private String probationDeliveryUnit;
	@CsvBindByPosition(position = 16)
	private String provider;
	@CsvBindByPosition(position = 17)
	private String roleNames;
}
