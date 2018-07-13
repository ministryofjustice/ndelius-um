package uk.co.bconline.ndelius.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse
{
	private String token;
	private int expiresIn;
}
