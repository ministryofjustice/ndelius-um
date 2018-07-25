package uk.co.bconline.ndelius.model;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {
	private String code;
	private String description;
	private Organisation organisation;
	private boolean active;
}
