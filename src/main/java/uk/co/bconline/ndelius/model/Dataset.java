package uk.co.bconline.ndelius.model;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
	@NotBlank
	private String code;
	private String description;
	private Organisation organisation;
	private boolean active;
}
