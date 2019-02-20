package uk.co.bconline.ndelius.test.util;

import uk.co.bconline.ndelius.model.Dataset;
import uk.co.bconline.ndelius.model.User;

import java.time.LocalDate;

import static java.util.Collections.singletonList;

public class UserUtils
{
	private static int nextTestUserId = 1;

	public static User aValidUser()
	{
		return User.builder()
				.username("test")
				.forenames("forenames")
				.surname("surname")
				.datasets(singletonList(Dataset.builder().code("N01").description("NPS London").build()))
				.homeArea(Dataset.builder().code("N01").description("NPS London").build())
				.startDate(LocalDate.of(2000, 1, 1))
				.privateSector(false)
				.build();
	}

	public static synchronized String nextTestUsername()
	{
		return "test.user" + String.valueOf(nextTestUserId++);
	}
}
