package uk.co.bconline.ndelius.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public final class Role
{
	private @NotBlank String name;
	private String description;
	private List<String> interactions;
}
