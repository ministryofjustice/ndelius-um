package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class ChangeNote
{
	@Schema(accessMode = READ_ONLY)
	private ChangeNote.User user;

	@Schema(accessMode = READ_ONLY)
	private LocalDateTime time;

	@Schema(accessMode = READ_ONLY)
	private String note;


	@Getter
	@Builder
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class User {
		@Schema(accessMode = READ_ONLY)
		private String username;

		@Schema(accessMode = READ_ONLY)
		private String forenames;

		@Schema(accessMode = READ_ONLY)
		private String surname;
	}
}