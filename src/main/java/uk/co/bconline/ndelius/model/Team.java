package uk.co.bconline.ndelius.model;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class Team
{
	private String code;
	private String description;
}