package uk.co.bconline.ndelius.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
	@NotBlank
	private String code;
	private String description;
	private Boolean active;
}
