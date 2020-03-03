package uk.co.bconline.ndelius.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

import static io.swagger.annotations.ApiModelProperty.AccessMode.READ_ONLY;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Modification
{
	@ApiModelProperty(accessMode = READ_ONLY)
	private String username;

	@ApiModelProperty(accessMode = READ_ONLY)
	private String forenames;

	@ApiModelProperty(accessMode = READ_ONLY)
	private String surname;

	@ApiModelProperty(accessMode = READ_ONLY)
	private LocalDateTime at;
}