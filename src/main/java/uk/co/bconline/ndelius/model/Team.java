package uk.co.bconline.ndelius.model;

import java.io.Serializable;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class Team implements Serializable
{
	private String code;
	private String description;
}