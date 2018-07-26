package uk.co.bconline.ndelius.model;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Organisation
{
	private String code;
	private String description;
}
