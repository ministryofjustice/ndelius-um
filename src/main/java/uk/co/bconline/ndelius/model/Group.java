package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"name", "type"})
public final class Group
{
	private @NotBlank String name;
	private @NotBlank String type;
	private String description;
	private List<String> members;
}