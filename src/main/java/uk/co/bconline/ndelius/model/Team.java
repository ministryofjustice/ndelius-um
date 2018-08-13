package uk.co.bconline.ndelius.model;

import javax.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class Team
{
	@NotBlank
	private String code;
	private String description;
}