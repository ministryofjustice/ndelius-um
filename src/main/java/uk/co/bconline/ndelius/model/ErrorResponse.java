package uk.co.bconline.ndelius.model;

import static java.util.Collections.singletonList;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse
{
	public ErrorResponse(String message)
	{
		this.error = singletonList(message);
	}

	private List<String> error;
}
