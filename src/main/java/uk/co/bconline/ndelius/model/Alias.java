package uk.co.bconline.ndelius.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.co.bconline.ndelius.validator.UniqueAliasUsername;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@UniqueAliasUsername
public class Alias
{
	@NotBlank
	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9_\\-.]*$", message = "invalid format")
	private String username;

	@Size(max = 60)
	@Pattern(regexp = "^[a-zA-Z0-9_\\-.]*$", message = "invalid format")
	private String aliasUsername;
}