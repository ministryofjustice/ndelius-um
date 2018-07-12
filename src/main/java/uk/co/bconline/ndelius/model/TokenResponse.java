package uk.co.bconline.ndelius.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse
{
	private String token;
	private int expiresIn;
}
