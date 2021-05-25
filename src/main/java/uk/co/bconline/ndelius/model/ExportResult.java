package uk.co.bconline.ndelius.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "username")
public final class ExportResult {
	private String username;
	private String forenames;
	private String surname;
	private String email;
	private String telephoneNumber;
	private LocalDate startDate;
	private LocalDate endDate;
	private String homeArea;
	private String datasets;
	private String sector;
	private String staffCode;
	private String staffGrade;
	private String teams;
}
