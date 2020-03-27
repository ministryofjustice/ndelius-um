package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
@Builder(toBuilder = true)
public final class Group
{
	private String name;
	private String description;
	private List<String> members;
}