package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.swagger.annotations.ApiModelProperty.AccessMode.READ_ONLY;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class ChangeNote
{
	@ApiModelProperty(accessMode = READ_ONLY)
	private ChangeNote.User user;

	@ApiModelProperty(accessMode = READ_ONLY)
	private LocalDateTime time;

	@ApiModelProperty(accessMode = READ_ONLY)
	private String note;


	@Getter
	@Builder
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class User {
		@ApiModelProperty(accessMode = READ_ONLY)
		private String username;

		@ApiModelProperty(accessMode = READ_ONLY)
		private String forenames;

		@ApiModelProperty(accessMode = READ_ONLY)
		private String surname;
	}
}