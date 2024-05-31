package uk.co.bconline.ndelius.model;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

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
	private String providerCode;
}